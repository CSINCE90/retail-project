package com.retailsports.product_service.controller.admin;

import com.retailsports.product_service.dto.request.DiscountRequest;
import com.retailsports.product_service.dto.response.ApiResponse;
import com.retailsports.product_service.dto.response.DiscountResponse;
import com.retailsports.product_service.service.DiscountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/discounts")
@RequiredArgsConstructor
@Slf4j
public class AdminDiscountController {

    private final DiscountService discountService;

    /**
     * POST /api/admin/discounts - Crea sconto
     */
    @PostMapping
    public ResponseEntity<ApiResponse<DiscountResponse>> createDiscount(@Valid @RequestBody DiscountRequest request) {
        log.info("POST /api/admin/discounts - Creating discount: {}", request.getName());

        DiscountResponse discount = discountService.createDiscount(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Discount created successfully", discount));
    }

    /**
     * PUT /api/admin/discounts/{id} - Aggiorna sconto
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DiscountResponse>> updateDiscount(
            @PathVariable Long id,
            @Valid @RequestBody DiscountRequest request
    ) {
        log.info("PUT /api/admin/discounts/{} - Updating discount", id);

        DiscountResponse discount = discountService.updateDiscount(id, request);
        return ResponseEntity.ok(ApiResponse.success("Discount updated successfully", discount));
    }

    /**
     * DELETE /api/admin/discounts/{id} - Elimina sconto
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteDiscount(@PathVariable Long id) {
        log.info("DELETE /api/admin/discounts/{} - Deleting discount", id);

        discountService.deleteDiscount(id);
        return ResponseEntity.ok(ApiResponse.success("Discount deleted successfully", null));
    }

    /**
     * GET /api/admin/discounts - Lista sconti
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<DiscountResponse>>> getAllDiscounts() {
        log.info("GET /api/admin/discounts");

        List<DiscountResponse> discounts = discountService.getActiveDiscounts();
        return ResponseEntity.ok(ApiResponse.success(discounts));
    }

    /**
     * GET /api/admin/discounts/active - Sconti attivi
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<DiscountResponse>>> getActiveDiscounts() {
        log.info("GET /api/admin/discounts/active");

        List<DiscountResponse> discounts = discountService.getValidDiscounts();
        return ResponseEntity.ok(ApiResponse.success(discounts));
    }

    /**
     * GET /api/admin/discounts/{id} - Dettaglio sconto
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DiscountResponse>> getDiscountById(@PathVariable Long id) {
        log.info("GET /api/admin/discounts/{}", id);

        DiscountResponse discount = discountService.getDiscountById(id);
        return ResponseEntity.ok(ApiResponse.success(discount));
    }

    /**
     * POST /api/admin/discounts/{discountId}/products/{productId} - Applica sconto a prodotto
     */
    @PostMapping("/{discountId}/products/{productId}")
    public ResponseEntity<ApiResponse<String>> applyDiscountToProduct(
            @PathVariable Long discountId,
            @PathVariable Long productId
    ) {
        log.info("POST /api/admin/discounts/{}/products/{} - Applying discount to product", discountId, productId);

        discountService.applyDiscountToProduct(discountId, productId);
        return ResponseEntity.ok(ApiResponse.success("Discount applied to product successfully", null));
    }

    /**
     * DELETE /api/admin/discounts/{discountId}/products/{productId} - Rimuovi sconto da prodotto
     */
    @DeleteMapping("/{discountId}/products/{productId}")
    public ResponseEntity<ApiResponse<String>> removeDiscountFromProduct(
            @PathVariable Long discountId,
            @PathVariable Long productId
    ) {
        log.info("DELETE /api/admin/discounts/{}/products/{} - Removing discount from product", discountId, productId);

        discountService.removeDiscountFromProduct(discountId, productId);
        return ResponseEntity.ok(ApiResponse.success("Discount removed from product successfully", null));
    }
}
