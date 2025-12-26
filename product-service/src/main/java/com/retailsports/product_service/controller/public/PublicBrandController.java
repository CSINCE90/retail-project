package com.retailsports.product_service.controller.public_;

import com.retailsports.product_service.dto.response.ApiResponse;
import com.retailsports.product_service.dto.response.BrandResponse;
import com.retailsports.product_service.service.BrandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
@Slf4j
public class PublicBrandController {

    private final BrandService brandService;

    /**
     * GET /api/brands - Tutti i brand
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<BrandResponse>>> getAllBrands() {
        log.info("GET /api/brands");

        List<BrandResponse> brands = brandService.getAllBrands();
        return ResponseEntity.ok(ApiResponse.success(brands));
    }

    /**
     * GET /api/brands/active - Brand attivi
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<BrandResponse>>> getActiveBrands() {
        log.info("GET /api/brands/active");

        List<BrandResponse> brands = brandService.getActiveBrands();
        return ResponseEntity.ok(ApiResponse.success(brands));
    }

    /**
     * GET /api/brands/{id} - Dettaglio brand
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BrandResponse>> getBrandById(@PathVariable Long id) {
        log.info("GET /api/brands/{}", id);

        BrandResponse brand = brandService.getBrandById(id);
        return ResponseEntity.ok(ApiResponse.success(brand));
    }

    /**
     * GET /api/brands/slug/{slug} - Brand per slug
     */
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<BrandResponse>> getBrandBySlug(@PathVariable String slug) {
        log.info("GET /api/brands/slug/{}", slug);

        BrandResponse brand = brandService.getBrandBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success(brand));
    }
}
