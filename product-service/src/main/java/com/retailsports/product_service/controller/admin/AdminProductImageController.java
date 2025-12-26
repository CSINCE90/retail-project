package com.retailsports.product_service.controller.admin;

import com.retailsports.product_service.dto.request.ProductImageRequest;
import com.retailsports.product_service.dto.response.ApiResponse;
import com.retailsports.product_service.dto.response.ProductImageResponse;
import com.retailsports.product_service.service.ProductImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products/{productId}/images")
@RequiredArgsConstructor
@Slf4j
public class AdminProductImageController {

    private final ProductImageService productImageService;

    /**
     * POST /api/admin/products/{productId}/images - Aggiungi immagine
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ProductImageResponse>> addImage(
            @PathVariable Long productId,
            @Valid @RequestBody ProductImageRequest request
    ) {
        log.info("POST /api/admin/products/{}/images - Adding image", productId);

        ProductImageResponse image = productImageService.addImage(productId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Image added successfully", image));
    }

    /**
     * GET /api/admin/products/{productId}/images - Ottieni tutte le immagini
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductImageResponse>>> getImages(@PathVariable Long productId) {
        log.info("GET /api/admin/products/{}/images", productId);

        List<ProductImageResponse> images = productImageService.getImagesByProduct(productId);
        return ResponseEntity.ok(ApiResponse.success(images));
    }

    /**
     * PUT /api/admin/products/{productId}/images/{imageId} - Aggiorna immagine
     */
    @PutMapping("/{imageId}")
    public ResponseEntity<ApiResponse<ProductImageResponse>> updateImage(
            @PathVariable Long productId,
            @PathVariable Long imageId,
            @Valid @RequestBody ProductImageRequest request
    ) {
        log.info("PUT /api/admin/products/{}/images/{} - Updating image", productId, imageId);

        ProductImageResponse image = productImageService.updateImage(imageId, request);
        return ResponseEntity.ok(ApiResponse.success("Image updated successfully", image));
    }

    /**
     * DELETE /api/admin/products/{productId}/images/{imageId} - Elimina immagine
     */
    @DeleteMapping("/{imageId}")
    public ResponseEntity<ApiResponse<String>> deleteImage(
            @PathVariable Long productId,
            @PathVariable Long imageId
    ) {
        log.info("DELETE /api/admin/products/{}/images/{} - Deleting image", productId, imageId);

        productImageService.deleteImage(imageId);
        return ResponseEntity.ok(ApiResponse.success("Image deleted successfully", null));
    }

    /**
     * PUT /api/admin/products/{productId}/images/{imageId}/set-primary - Imposta come principale
     */
    @PutMapping("/{imageId}/set-primary")
    public ResponseEntity<ApiResponse<ProductImageResponse>> setPrimaryImage(
            @PathVariable Long productId,
            @PathVariable Long imageId
    ) {
        log.info("PUT /api/admin/products/{}/images/{}/set-primary", productId, imageId);

        ProductImageResponse image = productImageService.setPrimaryImage(imageId);
        return ResponseEntity.ok(ApiResponse.success("Image set as primary successfully", image));
    }
}
