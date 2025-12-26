package com.retailsports.product_service.dto.response;

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
public class AttributeValueResponse {

    private Long id;
    private Long attributeId;
    private String attributeName;
    private String value;
    private String displayValue;
    private String colorHex;
    private Integer displayOrder;
    private LocalDateTime createdAt;
}
