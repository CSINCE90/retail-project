package com.retailsports.cart_service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO per un singolo item del carrello
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartItemResponse {

    /**
     * ID dell'item
     */
    private Long id;

    /**
     * ID del prodotto
     */
    private Long productId;

    /**
     * Quantità
     */
    private Integer quantity;

    /**
     * Prezzo unitario in centesimi
     */
    private Long unitPriceCents;

    /**
     * Percentuale di sconto applicata
     */
    private BigDecimal discountPercentage;

    /**
     * Subtotale (unitPriceCents * quantity)
     */
    private Long subtotalCents;

    /**
     * Ammontare dello sconto in centesimi
     */
    private Long discountAmountCents;

    /**
     * Prezzo finale (subtotal - discount)
     */
    private Long finalPriceCents;

    /**
     * Prezzo unitario formattato
     */
    private String unitPriceFormatted;

    /**
     * Subtotale formattato
     */
    private String subtotalFormatted;

    /**
     * Prezzo finale formattato
     */
    private String finalPriceFormatted;

    /**
     * Timestamp di aggiunta
     */
    private LocalDateTime addedAt;

    /**
     * Timestamp di ultimo aggiornamento
     */
    private LocalDateTime updatedAt;
}
