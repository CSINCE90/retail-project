package com.retailsports.product_service.controller.public_;

import com.retailsports.product_service.dto.response.ApiResponse;
import com.retailsports.product_service.dto.response.CategoryResponse;
import com.retailsports.product_service.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Slf4j
public class PublicCategoryController {

    private final CategoryService categoryService;

    /**
     * GET /api/categories - Tutte le categorie
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        log.info("GET /api/categories");

        List<CategoryResponse> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    /**
     * GET /api/categories/tree - Albero categorie (gerarchia completa)
     */
    @GetMapping("/tree")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategoryTree() {
        log.info("GET /api/categories/tree");

        List<CategoryResponse> categoryTree = categoryService.getCategoryTree();
        return ResponseEntity.ok(ApiResponse.success(categoryTree));
    }

    /**
     * GET /api/categories/{id} - Dettaglio categoria
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable Long id) {
        log.info("GET /api/categories/{}", id);

        CategoryResponse category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success(category));
    }

    /**
     * GET /api/categories/slug/{slug} - Categoria per slug
     */
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryBySlug(@PathVariable String slug) {
        log.info("GET /api/categories/slug/{}", slug);

        CategoryResponse category = categoryService.getCategoryBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success(category));
    }

    /**
     * GET /api/categories/{id}/subcategories - Sottocategorie
     */
    @GetMapping("/{id}/subcategories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getSubcategories(@PathVariable Long id) {
        log.info("GET /api/categories/{}/subcategories", id);

        List<CategoryResponse> subcategories = categoryService.getSubcategories(id);
        return ResponseEntity.ok(ApiResponse.success(subcategories));
    }

    /**
     * GET /api/categories/root - Categorie root (livello 1)
     */
    @GetMapping("/root")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getRootCategories() {
        log.info("GET /api/categories/root");

        List<CategoryResponse> rootCategories = categoryService.getRootCategories();
        return ResponseEntity.ok(ApiResponse.success(rootCategories));
    }
}
