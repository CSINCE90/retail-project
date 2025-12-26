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
public class BrandRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be less than 100 characters")
    private String name;

    @NotBlank(message = "Slug is required")
    @Size(max = 100, message = "Slug must be less than 100 characters")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug can only contain lowercase letters, numbers and hyphens")
    private String slug;

    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;

    @Size(max = 500, message = "Logo URL must be less than 500 characters")
    private String logoUrl;

    @Size(max = 255, message = "Website URL must be less than 255 characters")
    private String websiteUrl;

    @NotNull(message = "Active status is required")
    private Boolean isActive;

    // SEO
    @Size(max = 200, message = "Meta title must be less than 200 characters")
    private String metaTitle;

    @Size(max = 500, message = "Meta description must be less than 500 characters")
    private String metaDescription;
}
