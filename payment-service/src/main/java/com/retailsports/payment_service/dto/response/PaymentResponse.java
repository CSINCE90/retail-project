package com.retailsports.payment_service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentResponse {

    private Long id;
    private Long orderId;
    private String paymentMethod;
    private Long amountCents;
    private String currency;

    // Prezzo formattato
    private String amountFormatted;

    // Stato
    private String status;

    // Gateway esterno
    private String transactionId;
    private String paymentGateway;

    // Dettagli carta (solo ultime 4 cifre)
    private String cardLast4;
    private String cardBrand;

    // Note
    private String notes;
    private String errorMessage;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private LocalDateTime failedAt;

    // Campi calcolati
    private Boolean canBeRefunded;
}
