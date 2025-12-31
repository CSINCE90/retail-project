package com.retailsports.payment_service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponse {

    private Long id;
    private String orderNumber;
    private Long userId;

    // Prezzi (in centesimi)
    private Long subtotalCents;
    private Long discountCents;
    private Long shippingCents;
    private Long taxCents;
    private Long totalCents;

    // Prezzi formattati
    private String subtotalFormatted;
    private String totalFormatted;

    // Stati
    private String status;
    private String paymentStatus;
    private String paymentMethod;

    // Indirizzo di spedizione
    private String shippingAddressLine1;
    private String shippingAddressLine2;
    private String shippingCity;
    private String shippingState;
    private String shippingPostalCode;
    private String shippingCountry;

    // Indirizzo di fatturazione
    private String billingAddressLine1;
    private String billingAddressLine2;
    private String billingCity;
    private String billingState;
    private String billingPostalCode;
    private String billingCountry;

    // Note
    private String customerNotes;
    private String adminNotes;

    // Tracking
    private String trackingNumber;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime cancelledAt;

    // Relazioni
    private List<OrderItemResponse> items;
    private List<PaymentResponse> payments;
    private List<OrderStatusHistoryResponse> statusHistory;

    // Campi calcolati
    private Integer totalItems;
    private Boolean canBeCancelled;
    private Boolean canBeRefunded;
}
