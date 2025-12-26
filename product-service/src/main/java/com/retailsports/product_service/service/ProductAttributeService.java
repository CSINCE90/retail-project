package com.retailsports.product_service.service;

import com.retailsports.product_service.dto.request.ProductAttributeRequest;
import com.retailsports.product_service.dto.request.AttributeValueRequest;
import com.retailsports.product_service.dto.response.ProductAttributeResponse;
import com.retailsports.product_service.dto.response.AttributeValueResponse;
import com.retailsports.product_service.exception.BadRequestException;
import com.retailsports.product_service.exception.DuplicateResourceException;
import com.retailsports.product_service.exception.ResourceNotFoundException;
import com.retailsports.product_service.model.ProductAttribute;
import com.retailsports.product_service.model.AttributeValue;
import com.retailsports.product_service.repository.ProductAttributeRepository;
import com.retailsports.product_service.repository.AttributeValueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductAttributeService {

    private final ProductAttributeRepository attributeRepository;
    private final AttributeValueRepository attributeValueRepository;

    // ========== PRODUCT ATTRIBUTE METHODS ==========

    /**
     * Crea un nuovo attributo
     */
    public ProductAttributeResponse createAttribute(ProductAttributeRequest request) {
        log.info("Creating attribute with name: {}", request.getName());

        if (attributeRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Attribute with name '" + request.getName() + "' already exists");
        }

        ProductAttribute attribute = ProductAttribute.builder()
            .name(request.getName())
            .displayName(request.getDisplayName())
            .type(request.getType())
            .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
            .build();

        ProductAttribute saved = attributeRepository.save(attribute);
        log.info("Attribute created successfully with id: {}", saved.getId());

        return convertAttributeToResponse(saved);
    }

    /**
     * Aggiorna attributo esistente
     */
    public ProductAttributeResponse updateAttribute(Long id, ProductAttributeRequest request) {
        log.info("Updating attribute with id: {}", id);

        ProductAttribute attribute = attributeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Attribute not found with id: " + id));

        if (!attribute.getName().equals(request.getName()) && attributeRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Attribute with name '" + request.getName() + "' already exists");
        }

        attribute.setName(request.getName());
        attribute.setDisplayName(request.getDisplayName());
        attribute.setType(request.getType());
        attribute.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);

        ProductAttribute updated = attributeRepository.save(attribute);
        log.info("Attribute updated successfully with id: {}", updated.getId());

        return convertAttributeToResponse(updated);
    }

    /**
     * Ottieni attributo per ID
     */
    @Transactional(readOnly = true)
    public ProductAttributeResponse getAttributeById(Long id) {
        ProductAttribute attribute = attributeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Attribute not found with id: " + id));
        return convertAttributeToResponseWithValues(attribute);
    }

    /**
     * Ottieni tutti gli attributi
     */
    @Transactional(readOnly = true)
    public List<ProductAttributeResponse> getAllAttributes() {
        return attributeRepository.findAllOrderedByDisplayOrder()
            .stream()
            .map(this::convertAttributeToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Ottieni attributi per tipo
     */
    @Transactional(readOnly = true)
    public List<ProductAttributeResponse> getAttributesByType(ProductAttribute.AttributeType type) {
        return attributeRepository.findByType(type)
            .stream()
            .map(this::convertAttributeToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Elimina attributo
     */
    public void deleteAttribute(Long id) {
        log.info("Deleting attribute with id: {}", id);

        ProductAttribute attribute = attributeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Attribute not found with id: " + id));

        // Verifica che non ci siano valori associati
        long valueCount = attributeValueRepository.countByAttributeId(id);
        if (valueCount > 0) {
            throw new BadRequestException("Cannot delete attribute with values. Value count: " + valueCount);
        }

        attributeRepository.delete(attribute);
        log.info("Attribute deleted successfully with id: {}", id);
    }

    // ========== ATTRIBUTE VALUE METHODS ==========

    /**
     * Crea un nuovo valore per un attributo
     */
    public AttributeValueResponse createAttributeValue(AttributeValueRequest request) {
        log.info("Creating attribute value for attribute id: {}", request.getAttributeId());

        ProductAttribute attribute = attributeRepository.findById(request.getAttributeId())
            .orElseThrow(() -> new ResourceNotFoundException("Attribute not found with id: " + request.getAttributeId()));

        // Verifica unicità combinazione attribute-value
        if (attributeValueRepository.existsByAttributeIdAndValue(request.getAttributeId(), request.getValue())) {
            throw new DuplicateResourceException("Value '" + request.getValue() + "' already exists for this attribute");
        }

        AttributeValue value = AttributeValue.builder()
            .attribute(attribute)
            .value(request.getValue())
            .displayValue(request.getDisplayValue())
            .colorHex(request.getColorHex())
            .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
            .build();

        AttributeValue saved = attributeValueRepository.save(value);
        log.info("Attribute value created successfully with id: {}", saved.getId());

        return convertValueToResponse(saved);
    }

    /**
     * Aggiorna valore attributo
     */
    public AttributeValueResponse updateAttributeValue(Long id, AttributeValueRequest request) {
        log.info("Updating attribute value with id: {}", id);

        AttributeValue value = attributeValueRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Attribute value not found with id: " + id));

        // Se cambia l'attributo, verifica che esista
        if (!value.getAttribute().getId().equals(request.getAttributeId())) {
            ProductAttribute newAttribute = attributeRepository.findById(request.getAttributeId())
                .orElseThrow(() -> new ResourceNotFoundException("Attribute not found with id: " + request.getAttributeId()));
            value.setAttribute(newAttribute);
        }

        // Verifica unicità se il value cambia
        if (!value.getValue().equals(request.getValue())
            && attributeValueRepository.existsByAttributeIdAndValue(request.getAttributeId(), request.getValue())) {
            throw new DuplicateResourceException("Value '" + request.getValue() + "' already exists for this attribute");
        }

        value.setValue(request.getValue());
        value.setDisplayValue(request.getDisplayValue());
        value.setColorHex(request.getColorHex());
        value.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);

        AttributeValue updated = attributeValueRepository.save(value);
        log.info("Attribute value updated successfully with id: {}", updated.getId());

        return convertValueToResponse(updated);
    }

    /**
     * Ottieni valori per attributo
     */
    @Transactional(readOnly = true)
    public List<AttributeValueResponse> getValuesByAttribute(Long attributeId) {
        attributeRepository.findById(attributeId)
            .orElseThrow(() -> new ResourceNotFoundException("Attribute not found with id: " + attributeId));

        return attributeValueRepository.findByAttributeId(attributeId)
            .stream()
            .map(this::convertValueToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Elimina valore attributo
     */
    public void deleteAttributeValue(Long id) {
        log.info("Deleting attribute value with id: {}", id);

        AttributeValue value = attributeValueRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Attribute value not found with id: " + id));

        attributeValueRepository.delete(value);
        log.info("Attribute value deleted successfully with id: {}", id);
    }

    // ========== HELPER METHODS ==========

    private ProductAttributeResponse convertAttributeToResponse(ProductAttribute attribute) {
        return ProductAttributeResponse.builder()
            .id(attribute.getId())
            .name(attribute.getName())
            .displayName(attribute.getDisplayName())
            .type(attribute.getType())
            .displayOrder(attribute.getDisplayOrder())
            .createdAt(attribute.getCreatedAt())
            .build();
    }

    private ProductAttributeResponse convertAttributeToResponseWithValues(ProductAttribute attribute) {
        ProductAttributeResponse response = convertAttributeToResponse(attribute);

        List<AttributeValueResponse> values = attributeValueRepository.findByAttributeId(attribute.getId())
            .stream()
            .map(this::convertValueToResponse)
            .collect(Collectors.toList());

        response.setValues(values);
        return response;
    }

    private AttributeValueResponse convertValueToResponse(AttributeValue value) {
        return AttributeValueResponse.builder()
            .id(value.getId())
            .attributeId(value.getAttribute().getId())
            .attributeName(value.getAttribute().getName())
            .value(value.getValue())
            .displayValue(value.getDisplayValue())
            .colorHex(value.getColorHex())
            .displayOrder(value.getDisplayOrder())
            .createdAt(value.getCreatedAt())
            .build();
    }
}
