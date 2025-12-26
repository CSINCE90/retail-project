package com.retailsports.product_service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.retailsports.product_service.model.Discount.DiscountType;
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
public class DiscountResponse {

    private Long id;
    private String name;
    private String code;
    private String description;
    private DiscountType type;
    private Integer value;
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;
    private Integer maxUses;
    private Integer maxUsesPerUser;
    private Integer currentUses;
    private Integer minPurchaseAmountCents;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Campi calcolati
    private Boolean isValid; // Se lo sconto è valido al momento
    private Boolean isExpired; // Se lo sconto è scaduto
    private Integer remainingUses; // Utilizzi rimanenti
}
