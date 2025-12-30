package com.retailsports.cart_service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO per le risposte di errore
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    /**
     * Timestamp dell'errore
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * HTTP status code
     */
    private Integer status;

    /**
     * Tipo di errore
     */
    private String error;

    /**
     * Messaggio di errore principale
     */
    private String message;

    /**
     * Path della richiesta che ha generato l'errore
     */
    private String path;

    /**
     * Lista di errori di validazione (per Bean Validation)
     */
    private List<ValidationError> validationErrors;

    /**
     * Classe interna per errori di validazione
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationError {
        /**
         * Nome del campo che ha fallito la validazione
         */
        private String field;

        /**
         * Valore rifiutato
         */
        private Object rejectedValue;

        /**
         * Messaggio di errore
         */
        private String message;
    }
}
