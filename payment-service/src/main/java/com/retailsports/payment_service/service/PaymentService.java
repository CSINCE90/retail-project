package com.retailsports.payment_service.service;

import com.retailsports.payment_service.dto.request.ProcessPaymentRequest;
import com.retailsports.payment_service.dto.response.PaymentResponse;
import com.retailsports.payment_service.entity.Order;
import com.retailsports.payment_service.entity.Payment;
import com.retailsports.payment_service.enums.PaymentMethod;
import com.retailsports.payment_service.enums.PaymentStatus;
import com.retailsports.payment_service.exception.BadRequestException;
import com.retailsports.payment_service.exception.PaymentProcessingException;
import com.retailsports.payment_service.exception.ResourceNotFoundException;
import com.retailsports.payment_service.repository.OrderRepository;
import com.retailsports.payment_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    /**
     * Processa un pagamento per un ordine
     */
    public PaymentResponse processPayment(ProcessPaymentRequest request) {
        log.info("Processing payment for order: {}", request.getOrderId());

        // Validazione ordine
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + request.getOrderId()));

        // Verifica se già esiste un pagamento completato per questo ordine
        if (paymentRepository.hasCompletedPayment(request.getOrderId())) {
            throw new BadRequestException("Order already has a completed payment");
        }

        // Validazione amount
        if (request.getAmountCents() == null || request.getAmountCents() <= 0) {
            throw new BadRequestException("Invalid payment amount");
        }

        // Creazione pagamento
        Payment payment = Payment.builder()
                .order(order)
                .paymentMethod(PaymentMethod.valueOf(request.getPaymentMethod()))
                .amountCents(request.getAmountCents())
                .currency(request.getCurrency() != null ? request.getCurrency() : "EUR")
                .status(PaymentStatus.PENDING)
                .transactionId(request.getTransactionId())
                .paymentGateway(request.getPaymentGateway())
                .cardLast4(request.getCardLast4())
                .cardBrand(request.getCardBrand())
                .notes(request.getNotes())
                .build();

        payment = paymentRepository.save(payment);

        // Simulazione processamento pagamento
        try {
            boolean success = processPaymentWithGateway(payment);

            if (success) {
                payment.markAsCompleted();
                order.setPaymentStatus(com.retailsports.payment_service.enums.PaymentStatus.COMPLETED);
                log.info("Payment {} completed successfully", payment.getId());
            } else {
                payment.markAsFailed("Payment declined");
                log.warn("Payment {} failed", payment.getId());
            }
        } catch (Exception e) {
            payment.markAsFailed(e.getMessage());
            log.error("Payment {} processing error: {}", payment.getId(), e.getMessage());
        }

        payment = paymentRepository.save(payment);
        orderRepository.save(order);

        return convertToResponse(payment);
    }

    /**
     * Ottieni pagamento per ID
     */
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
        return convertToResponse(payment);
    }

    /**
     * Ottieni pagamenti per ordine
     */
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Ottieni tutti i pagamenti con filtri
     */
    @Transactional(readOnly = true)
    public Page<PaymentResponse> getAllPayments(String status, String paymentMethod,
                                               LocalDateTime startDate, LocalDateTime endDate,
                                               int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        PaymentStatus paymentStatus = status != null ? PaymentStatus.valueOf(status) : null;
        PaymentMethod method = paymentMethod != null ? PaymentMethod.valueOf(paymentMethod) : null;

        return paymentRepository.findWithFilters(paymentStatus, method, startDate, endDate, pageable)
                .map(this::convertToResponse);
    }

    /**
     * Rimborsa un pagamento
     */
    public PaymentResponse refundPayment(Long id, String reason) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));

        if (!payment.canBeRefunded()) {
            throw new PaymentProcessingException("Payment cannot be refunded in current state: " + payment.getStatus());
        }

        // Simulazione processo di rimborso
        try {
            boolean success = processRefundWithGateway(payment);

            if (success) {
                payment.markAsRefunded();

                // Aggiorna stato ordine
                Order order = payment.getOrder();
                order.setPaymentStatus(PaymentStatus.REFUNDED);
                orderRepository.save(order);

                log.info("Payment {} refunded successfully", payment.getId());
            } else {
                throw new PaymentProcessingException("Refund failed");
            }
        } catch (Exception e) {
            log.error("Refund error for payment {}: {}", payment.getId(), e.getMessage());
            throw new PaymentProcessingException("Refund processing failed: " + e.getMessage());
        }

        payment = paymentRepository.save(payment);
        return convertToResponse(payment);
    }

    // ========== HELPER METHODS ==========

    /**
     * Simulazione processamento pagamento con gateway esterno
     * In un'implementazione reale, qui si integrerebbe Stripe, PayPal, etc.
     */
    private boolean processPaymentWithGateway(Payment payment) {
        log.info("Processing payment with gateway: {}", payment.getPaymentGateway());

        // Simulazione: approva tutti i pagamenti per ora
        // In produzione, qui si chiamerebbe il gateway di pagamento reale

        switch (payment.getPaymentMethod()) {
            case CREDIT_CARD:
                // Simulazione chiamata a Stripe/PayPal
                return true;
            case PAYPAL:
                // Simulazione chiamata a PayPal
                return true;
            case BANK_TRANSFER:
                // Bonifico bancario - da confermare manualmente
                return false;
            case CASH_ON_DELIVERY:
                // Pagamento alla consegna
                return true;
            default:
                return false;
        }
    }

    /**
     * Simulazione processo di rimborso con gateway esterno
     */
    private boolean processRefundWithGateway(Payment payment) {
        log.info("Processing refund with gateway: {}", payment.getPaymentGateway());

        // Simulazione: approva tutti i rimborsi per ora
        // In produzione, qui si chiamerebbe il gateway di pagamento reale
        return true;
    }

    private PaymentResponse convertToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrder().getId())
                .paymentMethod(payment.getPaymentMethod().name())
                .amountCents(payment.getAmountCents())
                .currency(payment.getCurrency())
                .amountFormatted(formatCents(payment.getAmountCents()))
                .status(payment.getStatus().name())
                .transactionId(payment.getTransactionId())
                .paymentGateway(payment.getPaymentGateway())
                .cardLast4(payment.getCardLast4())
                .cardBrand(payment.getCardBrand())
                .notes(payment.getNotes())
                .errorMessage(payment.getErrorMessage())
                .createdAt(payment.getCreatedAt())
                .completedAt(payment.getCompletedAt())
                .failedAt(payment.getFailedAt())
                .canBeRefunded(payment.canBeRefunded())
                .build();
    }

    private String formatCents(Long cents) {
        if (cents == null) return null;
        return String.format("%.2f€", cents / 100.0);
    }
}
