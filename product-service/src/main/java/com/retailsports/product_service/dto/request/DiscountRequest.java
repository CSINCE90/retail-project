package com.retailsports.product_service.dto.request;

import com.retailsports.product_service.model.Discount.DiscountType;
import jakarta.validation.constraints.*;
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
public class DiscountRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be less than 100 characters")
    private String name;

    @Size(max = 50, message = "Code must be less than 50 characters")
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "Code can only contain uppercase letters, numbers and hyphens")
    private String code;

    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;

    @NotNull(message = "Discount type is required")
    private DiscountType type;

    @NotNull(message = "Value is required")
    @Min(value = 0, message = "Value must be at least 0")
    @Max(value = 100, message = "Percentage value must be at most 100")
    private Integer value;

    @NotNull(message = "Start date is required")
    private LocalDateTime startsAt;

    @NotNull(message = "End date is required")
    private LocalDateTime endsAt;

    @Min(value = 0, message = "Max uses must be at least 0")
    private Integer maxUses;

    @Min(value = 1, message = "Max uses per user must be at least 1")
    private Integer maxUsesPerUser;

    @Min(value = 0, message = "Min purchase amount must be at least 0")
    private Integer minPurchaseAmountCents;

    @NotNull(message = "Active status is required")
    private Boolean isActive;

    // IDs dei prodotti a cui applicare lo sconto
    private List<Long> productIds;
}
