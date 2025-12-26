package com.retailsports.product_service.service;

import com.retailsports.product_service.dto.request.ProductImageRequest;
import com.retailsports.product_service.dto.response.ProductImageResponse;
import com.retailsports.product_service.exception.ResourceNotFoundException;
import com.retailsports.product_service.model.Product;
import com.retailsports.product_service.model.ProductImage;
import com.retailsports.product_service.repository.ProductImageRepository;
import com.retailsports.product_service.repository.ProductRepository;
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
public class ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;

    /**
     * Aggiungi immagine a un prodotto
     */
    public ProductImageResponse addImage(Long productId, ProductImageRequest request) {
        log.info("Adding image to product with id: {}", productId);

        // Verifica che il prodotto esista
        Product product = productRepository.findActiveById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        // Se è immagine primary, rimuovi il flag da tutte le altre
        if (request.getIsPrimary() != null && request.getIsPrimary()) {
            productImageRepository.removePrimaryFlagForProduct(productId);
        }

        // Creazione immagine
        ProductImage image = ProductImage.builder()
            .product(product)
            .imageUrl(request.getImageUrl())
            .altText(request.getAltText())
            .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
            .isPrimary(request.getIsPrimary() != null ? request.getIsPrimary() : false)
            .build();

        ProductImage saved = productImageRepository.save(image);
        log.info("Image added successfully with id: {} to product: {}", saved.getId(), productId);

        return convertToResponse(saved);
    }

    /**
     * Aggiorna immagine esistente
     */
    public ProductImageResponse updateImage(Long imageId, ProductImageRequest request) {
        log.info("Updating image with id: {}", imageId);

        ProductImage image = productImageRepository.findById(imageId)
            .orElseThrow(() -> new ResourceNotFoundException("Image not found with id: " + imageId));

        // Se diventa primary, rimuovi il flag dalle altre immagini dello stesso prodotto
        if (request.getIsPrimary() != null && request.getIsPrimary() && !image.getIsPrimary()) {
            productImageRepository.removePrimaryFlagForProduct(image.getProduct().getId());
        }

        // Aggiornamento campi
        image.setImageUrl(request.getImageUrl());
        image.setAltText(request.getAltText());
        image.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        image.setIsPrimary(request.getIsPrimary() != null ? request.getIsPrimary() : false);

        ProductImage updated = productImageRepository.save(image);
        log.info("Image updated successfully with id: {}", updated.getId());

        return convertToResponse(updated);
    }

    /**
     * Ottieni tutte le immagini di un prodotto
     */
    @Transactional(readOnly = true)
    public List<ProductImageResponse> getImagesByProduct(Long productId) {
        // Verifica che il prodotto esista
        productRepository.findActiveById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        return productImageRepository.findByProductId(productId)
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Imposta un'immagine come primary (disattiva le altre)
     */
    public ProductImageResponse setPrimaryImage(Long imageId) {
        log.info("Setting image as primary with id: {}", imageId);

        ProductImage image = productImageRepository.findById(imageId)
            .orElseThrow(() -> new ResourceNotFoundException("Image not found with id: " + imageId));

        // Rimuovi flag primary da tutte le immagini del prodotto
        productImageRepository.removePrimaryFlagForProduct(image.getProduct().getId());

        // Imposta questa come primary
        image.setIsPrimary(true);
        ProductImage updated = productImageRepository.save(image);

        log.info("Image set as primary successfully with id: {}", updated.getId());
        return convertToResponse(updated);
    }

    /**
     * Elimina immagine
     */
    public void deleteImage(Long imageId) {
        log.info("Deleting image with id: {}", imageId);

        ProductImage image = productImageRepository.findById(imageId)
            .orElseThrow(() -> new ResourceNotFoundException("Image not found with id: " + imageId));

        productImageRepository.delete(image);
        log.info("Image deleted successfully with id: {}", imageId);
    }

    // ========== HELPER METHODS ==========

    /**
     * Converte ProductImage entity in ProductImageResponse DTO
     */
    private ProductImageResponse convertToResponse(ProductImage image) {
        return ProductImageResponse.builder()
            .id(image.getId())
            .imageUrl(image.getImageUrl())
            .altText(image.getAltText())
            .displayOrder(image.getDisplayOrder())
            .isPrimary(image.getIsPrimary())
            .createdAt(image.getCreatedAt())
            .build();
    }
}
