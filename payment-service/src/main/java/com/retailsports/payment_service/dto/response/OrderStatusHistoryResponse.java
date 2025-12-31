package com.retailsports.payment_service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class OrderStatusHistoryResponse {

    private Long id;
    private String oldStatus;
    private String newStatus;
    private Long changedByUserId;
    private Boolean changedByAdmin;
    private String notes;
    private LocalDateTime createdAt;
}
