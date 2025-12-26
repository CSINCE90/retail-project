package com.retailsports.product_service.controller.admin;

import com.retailsports.product_service.dto.request.ProductRequest;
import com.retailsports.product_service.dto.response.ApiResponse;
import com.retailsports.product_service.dto.response.ProductResponse;
import com.retailsports.product_service.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@Slf4j
public class AdminProductController {

    private final ProductService productService;

    /**
     * POST /api/admin/products - Crea prodotto
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductRequest request) {
        log.info("POST /api/admin/products - Creating product with SKU: {}", request.getSku());

        ProductResponse product = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created successfully", product));
    }

    /**
     * PUT /api/admin/products/{id} - Aggiorna prodotto
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request
    ) {
        log.info("PUT /api/admin/products/{} - Updating product", id);

        ProductResponse product = productService.updateProduct(id, request);
        return ResponseEntity.ok(ApiResponse.success("Product updated successfully", product));
    }

    /**
     * DELETE /api/admin/products/{id} - Soft delete prodotto
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable Long id) {
        log.info("DELETE /api/admin/products/{} - Soft deleting product", id);

        productService.softDeleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully", null));
    }

    /**
     * POST /api/admin/products/{id}/restore - Ripristina prodotto
     */
    @PostMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<ProductResponse>> restoreProduct(@PathVariable Long id) {
        log.info("POST /api/admin/products/{}/restore - Restoring product", id);

        ProductResponse product = productService.restoreProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product restored successfully", product));
    }

    /**
     * PUT /api/admin/products/{id}/stock - Aggiorna stock
     */
    @PutMapping("/{id}/stock")
    public ResponseEntity<ApiResponse<ProductResponse>> updateStock(
            @PathVariable Long id,
            @RequestBody StockUpdateRequest request
    ) {
        log.info("PUT /api/admin/products/{}/stock - Updating stock with quantity: {}", id, request.getQuantity());

        ProductResponse product = productService.updateStock(id, request.getQuantity());
        return ResponseEntity.ok(ApiResponse.success("Stock updated successfully", product));
    }

    /**
     * PUT /api/admin/products/{id}/toggle-active - Toggle active flag
     */
    @PutMapping("/{id}/toggle-active")
    public ResponseEntity<ApiResponse<ProductResponse>> toggleActive(@PathVariable Long id) {
        log.info("PUT /api/admin/products/{}/toggle-active", id);

        ProductResponse product = productService.getProductById(id);
        // Richiama update per cambiare lo stato
        ProductRequest updateRequest = buildUpdateRequestFromResponse(product);
        updateRequest.setIsActive(!product.getIsActive());

        ProductResponse updated = productService.updateProduct(id, updateRequest);
        return ResponseEntity.ok(ApiResponse.success("Active status toggled successfully", updated));
    }

    // ========== HELPER CLASSES ==========

    /**
     * DTO per aggiornamento stock
     */
    public static class StockUpdateRequest {
        private Integer quantity;

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }

    /**
     * Helper per convertire ProductResponse in ProductRequest (per toggle)
     */
    private ProductRequest buildUpdateRequestFromResponse(ProductResponse response) {
        return ProductRequest.builder()
                .sku(response.getSku())
                .barcode(response.getBarcode())
                .name(response.getName())
                .slug(response.getSlug())
                .description(response.getDescription())
                .longDescription(response.getLongDescription())
                .categoryId(response.getCategory().getId())
                .brandId(response.getBrand() != null ? response.getBrand().getId() : null)
                .priceCents(response.getPriceCents())
                .compareAtPriceCents(response.getCompareAtPriceCents())
                .costPriceCents(response.getCostPriceCents())
                .weightGrams(response.getWeightGrams())
                .lengthCm(response.getLengthCm())
                .widthCm(response.getWidthCm())
                .heightCm(response.getHeightCm())
                .stockQuantity(response.getStockQuantity())
                .lowStockThreshold(response.getLowStockThreshold())
                .trackInventory(response.getTrackInventory())
                .isActive(response.getIsActive())
                .isFeatured(response.getIsFeatured())
                .isNew(response.getIsNew())
                .metaTitle(response.getMetaTitle())
                .metaDescription(response.getMetaDescription())
                .metaKeywords(response.getMetaKeywords())
                .build();
    }
}
