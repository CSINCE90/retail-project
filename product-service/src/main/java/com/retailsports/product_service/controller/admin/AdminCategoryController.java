package com.retailsports.product_service.controller.admin;

import com.retailsports.product_service.dto.request.CategoryRequest;
import com.retailsports.product_service.dto.response.ApiResponse;
import com.retailsports.product_service.dto.response.CategoryResponse;
import com.retailsports.product_service.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
@Slf4j
public class AdminCategoryController {

    private final CategoryService categoryService;

    /**
     * POST /api/admin/categories - Crea categoria
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody CategoryRequest request) {
        log.info("POST /api/admin/categories - Creating category: {}", request.getName());

        CategoryResponse category = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Category created successfully", category));
    }

    /**
     * PUT /api/admin/categories/{id} - Aggiorna categoria
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request
    ) {
        log.info("PUT /api/admin/categories/{} - Updating category", id);

        CategoryResponse category = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(ApiResponse.success("Category updated successfully", category));
    }

    /**
     * DELETE /api/admin/categories/{id} - Elimina categoria
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCategory(@PathVariable Long id) {
        log.info("DELETE /api/admin/categories/{} - Deleting category", id);

        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully", null));
    }

    /**
     * PUT /api/admin/categories/{id}/toggle-active - Toggle active flag
     */
    @PutMapping("/{id}/toggle-active")
    public ResponseEntity<ApiResponse<CategoryResponse>> toggleActive(@PathVariable Long id) {
        log.info("PUT /api/admin/categories/{}/toggle-active", id);

        CategoryResponse category = categoryService.toggleActiveStatus(id);
        return ResponseEntity.ok(ApiResponse.success("Active status toggled successfully", category));
    }
}
