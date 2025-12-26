package com.retailsports.product_service.controller.public_;

import com.retailsports.product_service.dto.request.ProductSearchRequest;
import com.retailsports.product_service.dto.response.ApiResponse;
import com.retailsports.product_service.dto.response.PageResponse;
import com.retailsports.product_service.dto.response.ProductResponse;
import com.retailsports.product_service.dto.response.ProductSummaryResponse;
import com.retailsports.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class PublicProductController {

    private final ProductService productService;

    /**
     * GET /api/products - Catalogo prodotti (paginato)
     */
    @GetMapping
    public ResponseEntity<PageResponse<ProductSummaryResponse>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        log.info("GET /api/products - page: {}, size: {}, sortBy: {}, direction: {}", page, size, sortBy, direction);

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        ProductSearchRequest searchRequest = ProductSearchRequest.builder().build();
        Page<ProductSummaryResponse> products = productService.searchProducts(searchRequest, pageable);

        return ResponseEntity.ok(PageResponse.from(products));
    }

    /**
     * GET /api/products/search - Ricerca con filtri
     */
    @GetMapping("/search")
    public ResponseEntity<PageResponse<ProductSummaryResponse>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) Boolean featured,
            @RequestParam(required = false) Boolean newArrivals,
            @RequestParam(required = false) Boolean onSale,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        log.info("GET /api/products/search - keyword: {}, categoryId: {}, brandId: {}", keyword, categoryId, brandId);

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        ProductSearchRequest searchRequest = ProductSearchRequest.builder()
                .keyword(keyword)
                .categoryId(categoryId)
                .brandId(brandId)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .featured(featured)
                .newArrivals(newArrivals)
                .onSale(onSale)
                .inStock(inStock)
                .build();

        Page<ProductSummaryResponse> products = productService.searchProducts(searchRequest, pageable);

        return ResponseEntity.ok(PageResponse.from(products));
    }

    /**
     * GET /api/products/{id} - Dettaglio prodotto
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Long id) {
        log.info("GET /api/products/{}", id);

        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success(product));
    }

    /**
     * GET /api/products/slug/{slug} - Prodotto per slug
     */
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductBySlug(@PathVariable String slug) {
        log.info("GET /api/products/slug/{}", slug);

        ProductResponse product = productService.getProductBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success(product));
    }

    /**
     * GET /api/products/sku/{sku} - Prodotto per SKU
     */
    @GetMapping("/sku/{sku}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductBySku(@PathVariable String sku) {
        log.info("GET /api/products/sku/{}", sku);

        ProductResponse product = productService.getProductBySku(sku);
        return ResponseEntity.ok(ApiResponse.success(product));
    }

    /**
     * GET /api/products/featured - Prodotti in evidenza
     */
    @GetMapping("/featured")
    public ResponseEntity<PageResponse<ProductSummaryResponse>> getFeaturedProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("GET /api/products/featured - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ProductSummaryResponse> products = productService.getFeaturedProducts(pageable);

        return ResponseEntity.ok(PageResponse.from(products));
    }

    /**
     * GET /api/products/new - Nuovi arrivi
     */
    @GetMapping("/new")
    public ResponseEntity<PageResponse<ProductSummaryResponse>> getNewProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("GET /api/products/new - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ProductSummaryResponse> products = productService.getNewProducts(pageable);

        return ResponseEntity.ok(PageResponse.from(products));
    }

    /**
     * GET /api/products/on-sale - Prodotti in sconto
     */
    @GetMapping("/on-sale")
    public ResponseEntity<PageResponse<ProductSummaryResponse>> getProductsOnSale(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("GET /api/products/on-sale - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductSummaryResponse> products = productService.getProductsOnSale(pageable);

        return ResponseEntity.ok(PageResponse.from(products));
    }

    /**
     * GET /api/products/category/{categoryId} - Prodotti per categoria
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<PageResponse<ProductSummaryResponse>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        log.info("GET /api/products/category/{} - page: {}, size: {}", categoryId, page, size);

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<ProductSummaryResponse> products = productService.getProductsByCategory(categoryId, pageable);

        return ResponseEntity.ok(PageResponse.from(products));
    }

    /**
     * GET /api/products/brand/{brandId} - Prodotti per brand
     */
    @GetMapping("/brand/{brandId}")
    public ResponseEntity<PageResponse<ProductSummaryResponse>> getProductsByBrand(
            @PathVariable Long brandId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        log.info("GET /api/products/brand/{} - page: {}, size: {}", brandId, page, size);

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<ProductSummaryResponse> products = productService.getProductsByBrand(brandId, pageable);

        return ResponseEntity.ok(PageResponse.from(products));
    }
}
