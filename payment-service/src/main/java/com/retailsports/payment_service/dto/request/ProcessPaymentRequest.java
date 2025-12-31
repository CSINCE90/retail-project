package com.retailsports.payment_service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessPaymentRequest {

    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    @NotNull(message = "Amount is required")
    @Min(value = 0, message = "Amount must be at least 0")
    private Long amountCents;

    @Size(max = 3, message = "Currency must be 3 characters")
    private String currency;

    // Dettagli carta (tokenizzati)
    @Size(max = 4, message = "Card last 4 must be 4 characters")
    private String cardLast4;

    @Size(max = 50, message = "Card brand must be less than 50 characters")
    private String cardBrand;

    // Gateway esterno
    @Size(max = 255, message = "Transaction ID must be less than 255 characters")
    private String transactionId;

    @Size(max = 50, message = "Payment gateway must be less than 50 characters")
    private String paymentGateway;

    @Size(max = 1000, message = "Notes must be less than 1000 characters")
    private String notes;
}
