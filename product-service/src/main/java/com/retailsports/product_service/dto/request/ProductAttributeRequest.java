package com.retailsports.product_service.dto.request;

import com.retailsports.product_service.model.ProductAttribute.AttributeType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductAttributeRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Name must be less than 50 characters")
    @Pattern(regexp = "^[a-z_]+$", message = "Name can only contain lowercase letters and underscores")
    private String name;

    @NotBlank(message = "Display name is required")
    @Size(max = 100, message = "Display name must be less than 100 characters")
    private String displayName;

    @NotNull(message = "Type is required")
    private AttributeType type;

    @Min(value = 0, message = "Display order must be at least 0")
    private Integer displayOrder;
}
