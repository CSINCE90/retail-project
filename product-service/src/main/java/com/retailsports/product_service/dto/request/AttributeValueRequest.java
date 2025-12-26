package com.retailsports.product_service.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttributeValueRequest {

    @NotNull(message = "Attribute ID is required")
    private Long attributeId;

    @NotBlank(message = "Value is required")
    @Size(max = 100, message = "Value must be less than 100 characters")
    private String value;

    @NotBlank(message = "Display value is required")
    @Size(max = 100, message = "Display value must be less than 100 characters")
    private String displayValue;

    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color hex must be a valid hex color (e.g., #FF0000)")
    private String colorHex;

    @Min(value = 0, message = "Display order must be at least 0")
    private Integer displayOrder;
}
