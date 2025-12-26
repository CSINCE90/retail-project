package com.retailsports.product_service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.retailsports.product_service.model.ProductAttribute.AttributeType;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductAttributeResponse {

    private Long id;
    private String name;
    private String displayName;
    private AttributeType type;
    private Integer displayOrder;
    private LocalDateTime createdAt;

    // Campo opzionale per includere i valori
    private List<AttributeValueResponse> values;
}
