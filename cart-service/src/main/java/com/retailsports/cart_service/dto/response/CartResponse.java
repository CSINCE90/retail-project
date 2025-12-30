package com.retailsports.cart_service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO per il carrello completo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartResponse {

    /**
     * ID del carrello
     */
    private Long id;

    /**
     * ID dell'utente proprietario
     */
    private Long userId;

    /**
     * Lista degli items nel carrello
     */
    private List<CartItemResponse> items;

    /**
     * Numero totale di items (somma delle quantità)
     */
    private Integer totalItems;

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

    /**
     * Timestamp di creazione
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp di ultimo aggiornamento
     */
    private LocalDateTime updatedAt;
}
