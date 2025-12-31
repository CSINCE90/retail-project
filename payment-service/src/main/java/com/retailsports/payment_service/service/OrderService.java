package com.retailsports.payment_service.service;

import com.retailsports.payment_service.dto.request.CreateOrderRequest;
import com.retailsports.payment_service.dto.request.OrderItemRequest;
import com.retailsports.payment_service.dto.request.UpdateOrderStatusRequest;
import com.retailsports.payment_service.dto.response.*;
import com.retailsports.payment_service.entity.Order;
import com.retailsports.payment_service.entity.OrderItem;
import com.retailsports.payment_service.enums.OrderStatus;
import com.retailsports.payment_service.enums.PaymentMethod;
import com.retailsports.payment_service.enums.PaymentStatus;
import com.retailsports.payment_service.exception.BadRequestException;
import com.retailsports.payment_service.exception.DuplicateResourceException;
import com.retailsports.payment_service.exception.InvalidOrderStateException;
import com.retailsports.payment_service.exception.ResourceNotFoundException;
import com.retailsports.payment_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    /**
     * Crea un nuovo ordine
     */
    public OrderResponse createOrder(CreateOrderRequest request) {
        log.info("Creating order for user: {}", request.getUserId());

        // Validazione items
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BadRequestException("Order must contain at least one item");
        }

        // Generazione numero ordine
        String orderNumber = generateOrderNumber();

        // Calcolo totali
        long subtotal = 0L;
        for (OrderItemRequest itemReq : request.getItems()) {
            long itemSubtotal = itemReq.getUnitPriceCents() * itemReq.getQuantity();
            long itemDiscount = itemReq.getDiscountCents() != null ? itemReq.getDiscountCents() : 0L;
            subtotal += (itemSubtotal - itemDiscount);
        }

        long discountCents = request.getDiscountCents() != null ? request.getDiscountCents() : 0L;
        long shippingCents = request.getShippingCents() != null ? request.getShippingCents() : 0L;
        long taxCents = calculateTax(subtotal - discountCents); // 22% IVA
        long totalCents = subtotal - discountCents + shippingCents + taxCents;

        // Creazione ordine
        Order order = Order.builder()
                .orderNumber(orderNumber)
                .userId(request.getUserId())
                .subtotalCents(subtotal)
                .discountCents(discountCents)
                .shippingCents(shippingCents)
                .taxCents(taxCents)
                .totalCents(totalCents)
                .status(OrderStatus.PENDING)
                .paymentStatus(PaymentStatus.PENDING)
                .paymentMethod(PaymentMethod.valueOf(request.getPaymentMethod()))
                .shippingAddressLine1(request.getShippingAddressLine1())
                .shippingAddressLine2(request.getShippingAddressLine2())
                .shippingCity(request.getShippingCity())
                .shippingState(request.getShippingState())
                .shippingPostalCode(request.getShippingPostalCode())
                .shippingCountry(request.getShippingCountry())
                .billingAddressLine1(request.getBillingAddressLine1())
                .billingAddressLine2(request.getBillingAddressLine2())
                .billingCity(request.getBillingCity())
                .billingState(request.getBillingState())
                .billingPostalCode(request.getBillingPostalCode())
                .billingCountry(request.getBillingCountry())
                .customerNotes(request.getCustomerNotes())
                .build();

        // Aggiunta items
        for (OrderItemRequest itemReq : request.getItems()) {
            OrderItem item = OrderItem.builder()
                    .productId(itemReq.getProductId())
                    .productName(itemReq.getProductName())
                    .productSku(itemReq.getProductSku())
                    .productImage(itemReq.getProductImage())
                    .quantity(itemReq.getQuantity())
                    .unitPriceCents(itemReq.getUnitPriceCents())
                    .discountPercentage(itemReq.getDiscountPercentage())
                    .discountCents(itemReq.getDiscountCents() != null ? itemReq.getDiscountCents() : 0L)
                    .build();

            item.calculateTotals();
            order.addItem(item);
        }

        order = orderRepository.save(order);
        log.info("Order created with number: {}", orderNumber);

        return convertToResponse(order);
    }

    /**
     * Ottieni ordine per ID
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return convertToResponse(order);
    }

    /**
     * Ottieni ordine per numero ordine
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrderByOrderNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with number: " + orderNumber));
        return convertToResponse(order);
    }

    /**
     * Ottieni ordini per utente
     */
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByUserId(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return orderRepository.findByUserId(userId, pageable)
                .map(this::convertToResponse);
    }

    /**
     * Ottieni tutti gli ordini con filtri
     */
    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(Long userId, String status, String paymentStatus,
                                           LocalDateTime startDate, LocalDateTime endDate,
                                           int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        OrderStatus orderStatus = status != null ? OrderStatus.valueOf(status) : null;
        PaymentStatus paymentStat = paymentStatus != null ? PaymentStatus.valueOf(paymentStatus) : null;

        return orderRepository.findWithFilters(userId, orderStatus, paymentStat, startDate, endDate, pageable)
                .map(this::convertToResponse);
    }

    /**
     * Aggiorna stato ordine
     */
    public OrderResponse updateOrderStatus(Long id, UpdateOrderStatusRequest request, Long userId, boolean isAdmin) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        OrderStatus newStatus = OrderStatus.valueOf(request.getStatus());

        // Validazione transizione stato
        if (!isValidStatusTransition(order.getStatus(), newStatus)) {
            throw new InvalidOrderStateException(
                    "Cannot transition from " + order.getStatus() + " to " + newStatus);
        }

        // Aggiorna tracking number se presente
        if (request.getTrackingNumber() != null) {
            order.setTrackingNumber(request.getTrackingNumber());
        }

        // Aggiorna stato con storico
        order.updateStatus(newStatus, userId, isAdmin, request.getNotes());

        order = orderRepository.save(order);
        log.info("Order {} status updated to {}", id, newStatus);

        return convertToResponse(order);
    }

    /**
     * Cancella ordine
     */
    public OrderResponse cancelOrder(Long id, Long userId, String reason) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        if (!order.canBeCancelled()) {
            throw new InvalidOrderStateException("Order cannot be cancelled in current state: " + order.getStatus());
        }

        order.updateStatus(OrderStatus.CANCELLED, userId, false, reason);
        order = orderRepository.save(order);

        log.info("Order {} cancelled", id);
        return convertToResponse(order);
    }

    // ========== HELPER METHODS ==========

    private String generateOrderNumber() {
        int currentYear = Year.now().getValue();
        long count = orderRepository.count() + 1;
        return String.format("ORD-%d-%06d", currentYear, count);
    }

    private long calculateTax(long amount) {
        // IVA 22%
        return (long) (amount * 0.22);
    }

    private boolean isValidStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        // Logica di validazione transizioni
        return switch (currentStatus) {
            case PENDING -> newStatus == OrderStatus.CONFIRMED || newStatus == OrderStatus.CANCELLED;
            case CONFIRMED -> newStatus == OrderStatus.PROCESSING || newStatus == OrderStatus.CANCELLED;
            case PROCESSING -> newStatus == OrderStatus.SHIPPED || newStatus == OrderStatus.CANCELLED;
            case SHIPPED -> newStatus == OrderStatus.DELIVERED;
            case DELIVERED -> newStatus == OrderStatus.REFUNDED;
            case CANCELLED, REFUNDED -> false;
        };
    }

    private OrderResponse convertToResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .userId(order.getUserId())
                .subtotalCents(order.getSubtotalCents())
                .discountCents(order.getDiscountCents())
                .shippingCents(order.getShippingCents())
                .taxCents(order.getTaxCents())
                .totalCents(order.getTotalCents())
                .subtotalFormatted(formatCents(order.getSubtotalCents()))
                .totalFormatted(formatCents(order.getTotalCents()))
                .status(order.getStatus().name())
                .paymentStatus(order.getPaymentStatus().name())
                .paymentMethod(order.getPaymentMethod() != null ? order.getPaymentMethod().name() : null)
                .shippingAddressLine1(order.getShippingAddressLine1())
                .shippingAddressLine2(order.getShippingAddressLine2())
                .shippingCity(order.getShippingCity())
                .shippingState(order.getShippingState())
                .shippingPostalCode(order.getShippingPostalCode())
                .shippingCountry(order.getShippingCountry())
                .billingAddressLine1(order.getBillingAddressLine1())
                .billingAddressLine2(order.getBillingAddressLine2())
                .billingCity(order.getBillingCity())
                .billingState(order.getBillingState())
                .billingPostalCode(order.getBillingPostalCode())
                .billingCountry(order.getBillingCountry())
                .customerNotes(order.getCustomerNotes())
                .adminNotes(order.getAdminNotes())
                .trackingNumber(order.getTrackingNumber())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .confirmedAt(order.getConfirmedAt())
                .shippedAt(order.getShippedAt())
                .deliveredAt(order.getDeliveredAt())
                .cancelledAt(order.getCancelledAt())
                .items(order.getItems().stream()
                        .map(this::convertItemToResponse)
                        .collect(Collectors.toList()))
                .totalItems(order.getItems().size())
                .canBeCancelled(order.canBeCancelled())
                .canBeRefunded(order.canBeRefunded())
                .build();
    }

    private OrderItemResponse convertItemToResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .productName(item.getProductName())
                .productSku(item.getProductSku())
                .productImage(item.getProductImage())
                .quantity(item.getQuantity())
                .unitPriceCents(item.getUnitPriceCents())
                .discountPercentage(item.getDiscountPercentage())
                .discountCents(item.getDiscountCents())
                .subtotalCents(item.getSubtotalCents())
                .totalCents(item.getTotalCents())
                .unitPriceFormatted(formatCents(item.getUnitPriceCents()))
                .totalFormatted(formatCents(item.getTotalCents()))
                .createdAt(item.getCreatedAt())
                .build();
    }

    private String formatCents(Long cents) {
        if (cents == null) return null;
        return String.format("%.2f€", cents / 100.0);
    }
}
