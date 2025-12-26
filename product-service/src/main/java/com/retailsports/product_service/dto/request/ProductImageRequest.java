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
public class ProductImageRequest {

    @NotBlank(message = "Image URL is required")
    @Size(max = 500, message = "Image URL must be less than 500 characters")
    private String imageUrl;

    @Size(max = 255, message = "Alt text must be less than 255 characters")
    private String altText;

    @Min(value = 0, message = "Display order must be at least 0")
    private Integer displayOrder;

    private Boolean isPrimary;
}
