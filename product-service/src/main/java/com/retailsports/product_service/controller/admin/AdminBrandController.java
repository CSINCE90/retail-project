package com.retailsports.product_service.controller.admin;

import com.retailsports.product_service.dto.request.BrandRequest;
import com.retailsports.product_service.dto.response.ApiResponse;
import com.retailsports.product_service.dto.response.BrandResponse;
import com.retailsports.product_service.service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/brands")
@RequiredArgsConstructor
@Slf4j
public class AdminBrandController {

    private final BrandService brandService;

    /**
     * POST /api/admin/brands - Crea brand
     */
    @PostMapping
    public ResponseEntity<ApiResponse<BrandResponse>> createBrand(@Valid @RequestBody BrandRequest request) {
        log.info("POST /api/admin/brands - Creating brand: {}", request.getName());

        BrandResponse brand = brandService.createBrand(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Brand created successfully", brand));
    }

    /**
     * PUT /api/admin/brands/{id} - Aggiorna brand
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BrandResponse>> updateBrand(
            @PathVariable Long id,
            @Valid @RequestBody BrandRequest request
    ) {
        log.info("PUT /api/admin/brands/{} - Updating brand", id);

        BrandResponse brand = brandService.updateBrand(id, request);
        return ResponseEntity.ok(ApiResponse.success("Brand updated successfully", brand));
    }

    /**
     * DELETE /api/admin/brands/{id} - Elimina brand
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteBrand(@PathVariable Long id) {
        log.info("DELETE /api/admin/brands/{} - Deleting brand", id);

        brandService.deleteBrand(id);
        return ResponseEntity.ok(ApiResponse.success("Brand deleted successfully", null));
    }

    /**
     * PUT /api/admin/brands/{id}/toggle-active - Toggle active flag
     */
    @PutMapping("/{id}/toggle-active")
    public ResponseEntity<ApiResponse<BrandResponse>> toggleActive(@PathVariable Long id) {
        log.info("PUT /api/admin/brands/{}/toggle-active", id);

        BrandResponse brand = brandService.toggleActiveStatus(id);
        return ResponseEntity.ok(ApiResponse.success("Active status toggled successfully", brand));
    }
}
