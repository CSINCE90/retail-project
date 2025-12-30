package com.retailsports.cart_service.service;

import com.retailsports.cart_service.exception.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Client per comunicare con il Product Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceClient {

    private final RestTemplate restTemplate;

    @Value("${services.product-service.url}")
    private String productServiceUrl;

    /**
     * DTO interno per la risposta del Product Service
     */
    public static class ProductInfo {
        private Long id;
        private String name;
        private Integer priceCents;
        private Integer stockQuantity;
        private Boolean trackInventory;
        private Boolean isActive;

        // Getters e setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getPriceCents() { return priceCents; }
        public void setPriceCents(Integer priceCents) { this.priceCents = priceCents; }
        public Integer getStockQuantity() { return stockQuantity; }
        public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
        public Boolean getTrackInventory() { return trackInventory; }
        public void setTrackInventory(Boolean trackInventory) { this.trackInventory = trackInventory; }
        public Boolean getIsActive() { return isActive; }
        public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    }

    /**
     * Recupera le informazioni di un prodotto dal Product Service
     */
    public ProductInfo getProduct(Long productId) {
        String url = productServiceUrl + "/api/products/" + productId;

        log.info("Fetching product info from Product Service: {}", url);

        try {
            ProductInfo productInfo = restTemplate.getForObject(url, ProductInfo.class);

            if (productInfo == null) {
                throw new ProductNotFoundException("Product not found with id: " + productId);
            }

            log.info("Product info retrieved successfully: {}", productInfo.getName());
            return productInfo;

        } catch (HttpClientErrorException.NotFound ex) {
            log.error("Product not found in Product Service: {}", productId);
            throw new ProductNotFoundException("Product not found with id: " + productId);
        } catch (Exception ex) {
            log.error("Error calling Product Service for product {}: {}", productId, ex.getMessage());
            throw new RuntimeException("Error calling Product Service: " + ex.getMessage(), ex);
        }
    }

    /**
     * Verifica se il prodotto è disponibile e ha stock sufficiente
     */
    public void validateProductStock(Long productId, Integer requestedQuantity) {
        ProductInfo product = getProduct(productId);

        // Verifica che il prodotto sia attivo
        if (product.getIsActive() == null || !product.getIsActive()) {
            throw new ProductNotFoundException("Product is not active: " + productId);
        }

        // Se il prodotto traccia l'inventario, verifica lo stock
        if (product.getTrackInventory() != null && product.getTrackInventory()) {
            Integer availableStock = product.getStockQuantity() != null ? product.getStockQuantity() : 0;

            if (availableStock < requestedQuantity) {
                throw new com.retailsports.cart_service.exception.InsufficientStockException(
                    String.format("Insufficient stock for product %d. Available: %d, Requested: %d",
                        productId, availableStock, requestedQuantity)
                );
            }
        }

        log.info("Product {} has sufficient stock ({} requested)", productId, requestedQuantity);
    }
}
