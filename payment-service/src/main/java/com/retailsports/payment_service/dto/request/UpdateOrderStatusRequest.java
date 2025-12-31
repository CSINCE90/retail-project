package com.retailsports.payment_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateOrderStatusRequest {

    @NotBlank(message = "Status is required")
    private String status;

    @Size(max = 1000, message = "Notes must be less than 1000 characters")
    private String notes;

    @Size(max = 100, message = "Tracking number must be less than 100 characters")
    private String trackingNumber;
}
