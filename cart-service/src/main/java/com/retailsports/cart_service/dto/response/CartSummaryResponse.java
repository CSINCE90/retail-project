package com.retailsports.cart_service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO per il riepilogo del carrello (senza dettagli items)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartSummaryResponse {

    /**
     * ID del carrello
     */
    private Long id;

    /**
     * ID dell'utente proprietario
     */
    private Long userId;

    /**
     * Numero totale di items (somma delle quantità)
     */
    private Integer totalItems;

    /**
     * Numero di prodotti distinti
     */
    private Integer uniqueProducts;

    /**
     * Subtotale del carrello in centesimi
     */
    private Long subtotalCents;

    /**
     * Totale sconti applicati in centesimi
     */
    private Long totalDiscountCents;

    /**
     * Totale finale in centesimi
     */
    private Long totalCents;

    /**
     * Subtotale formattato
     */
    private String subtotalFormatted;

    /**
     * Totale sconti formattato
     */
    private String totalDiscountFormatted;

    /**
     * Totale finale formattato
     */
    private String totalFormatted;
}
