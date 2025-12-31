package com.retailsports.payment_service.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Order items are required")
    @Size(min = 1, message = "At least one order item is required")
    @Valid
    private List<OrderItemRequest> items;

    // Metodo pagamento
    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    // Indirizzo di spedizione
    @NotBlank(message = "Shipping address line 1 is required")
    @Size(max = 255, message = "Shipping address line 1 must be less than 255 characters")
    private String shippingAddressLine1;

    @Size(max = 255, message = "Shipping address line 2 must be less than 255 characters")
    private String shippingAddressLine2;

    @NotBlank(message = "Shipping city is required")
    @Size(max = 100, message = "Shipping city must be less than 100 characters")
    private String shippingCity;

    @Size(max = 100, message = "Shipping state must be less than 100 characters")
    private String shippingState;

    @NotBlank(message = "Shipping postal code is required")
    @Size(max = 20, message = "Shipping postal code must be less than 20 characters")
    private String shippingPostalCode;

    @NotBlank(message = "Shipping country is required")
    @Size(max = 100, message = "Shipping country must be less than 100 characters")
    private String shippingCountry;

    // Indirizzo di fatturazione
    @NotBlank(message = "Billing address line 1 is required")
    @Size(max = 255, message = "Billing address line 1 must be less than 255 characters")
    private String billingAddressLine1;

    @Size(max = 255, message = "Billing address line 2 must be less than 255 characters")
    private String billingAddressLine2;

    @NotBlank(message = "Billing city is required")
    @Size(max = 100, message = "Billing city must be less than 100 characters")
    private String billingCity;

    @Size(max = 100, message = "Billing state must be less than 100 characters")
    private String billingState;

    @NotBlank(message = "Billing postal code is required")
    @Size(max = 20, message = "Billing postal code must be less than 20 characters")
    private String billingPostalCode;

    @NotBlank(message = "Billing country is required")
    @Size(max = 100, message = "Billing country must be less than 100 characters")
    private String billingCountry;

    // Note cliente
    @Size(max = 1000, message = "Customer notes must be less than 1000 characters")
    private String customerNotes;

    // Costi aggiuntivi
    @Min(value = 0, message = "Shipping cents must be at least 0")
    private Long shippingCents;

    @Min(value = 0, message = "Discount cents must be at least 0")
    private Long discountCents;
}
