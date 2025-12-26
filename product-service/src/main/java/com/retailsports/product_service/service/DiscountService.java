package com.retailsports.product_service.service;

import com.retailsports.product_service.dto.request.DiscountRequest;
import com.retailsports.product_service.dto.response.DiscountResponse;
import com.retailsports.product_service.exception.BadRequestException;
import com.retailsports.product_service.exception.DuplicateResourceException;
import com.retailsports.product_service.exception.ResourceNotFoundException;
import com.retailsports.product_service.model.Discount;
import com.retailsports.product_service.model.Product;
import com.retailsports.product_service.repository.DiscountRepository;
import com.retailsports.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DiscountService {

    private final DiscountRepository discountRepository;
    private final ProductRepository productRepository;

    /**
     * Crea un nuovo sconto
     */
    public DiscountResponse createDiscount(DiscountRequest request) {
        log.info("Creating discount with name: {}", request.getName());

        // Validazione code univoco (se fornito)
        if (request.getCode() != null && discountRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Discount with code '" + request.getCode() + "' already exists");
        }

        // Validazione date
        if (request.getEndsAt().isBefore(request.getStartsAt())) {
            throw new BadRequestException("End date must be after start date");
        }

        // Validazione valore percentuale
        if (request.getType() == Discount.DiscountType.PERCENTAGE && request.getValue() > 100) {
            throw new BadRequestException("Percentage value cannot exceed 100");
        }

        // Creazione sconto
        Discount discount = Discount.builder()
            .name(request.getName())
            .code(request.getCode())
            .description(request.getDescription())
            .type(request.getType())
            .value(request.getValue())
            .startsAt(request.getStartsAt())
            .endsAt(request.getEndsAt())
            .maxUses(request.getMaxUses())
            .maxUsesPerUser(request.getMaxUsesPerUser() != null ? request.getMaxUsesPerUser() : 1)
            .minPurchaseAmountCents(request.getMinPurchaseAmountCents())
            .isActive(request.getIsActive())
            .build();

        Discount saved = discountRepository.save(discount);

        // Associa prodotti (se forniti)
        if (request.getProductIds() != null && !request.getProductIds().isEmpty()) {
            for (Long productId : request.getProductIds()) {
                applyDiscountToProduct(saved.getId(), productId);
            }
        }

        log.info("Discount created successfully with id: {}", saved.getId());
        return convertToResponse(saved);
    }

    /**
     * Aggiorna uno sconto esistente
     */
    public DiscountResponse updateDiscount(Long id, DiscountRequest request) {
        log.info("Updating discount with id: {}", id);

        Discount discount = discountRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Discount not found with id: " + id));

        // Validazione code (se cambiato)
        if (request.getCode() != null && !request.getCode().equals(discount.getCode())
            && discountRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Discount with code '" + request.getCode() + "' already exists");
        }

        // Validazione date
        if (request.getEndsAt().isBefore(request.getStartsAt())) {
            throw new BadRequestException("End date must be after start date");
        }

        // Validazione valore percentuale
        if (request.getType() == Discount.DiscountType.PERCENTAGE && request.getValue() > 100) {
            throw new BadRequestException("Percentage value cannot exceed 100");
        }

        // Aggiornamento campi
        discount.setName(request.getName());
        discount.setCode(request.getCode());
        discount.setDescription(request.getDescription());
        discount.setType(request.getType());
        discount.setValue(request.getValue());
        discount.setStartsAt(request.getStartsAt());
        discount.setEndsAt(request.getEndsAt());
        discount.setMaxUses(request.getMaxUses());
        discount.setMaxUsesPerUser(request.getMaxUsesPerUser() != null ? request.getMaxUsesPerUser() : 1);
        discount.setMinPurchaseAmountCents(request.getMinPurchaseAmountCents());
        discount.setIsActive(request.getIsActive());

        Discount updated = discountRepository.save(discount);
        log.info("Discount updated successfully with id: {}", updated.getId());

        return convertToResponse(updated);
    }

    /**
     * Ottieni sconto per ID
     */
    @Transactional(readOnly = true)
    public DiscountResponse getDiscountById(Long id) {
        Discount discount = discountRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Discount not found with id: " + id));
        return convertToResponse(discount);
    }

    /**
     * Ottieni sconto per codice
     */
    @Transactional(readOnly = true)
    public DiscountResponse getDiscountByCode(String code) {
        Discount discount = discountRepository.findByCode(code)
            .orElseThrow(() -> new ResourceNotFoundException("Discount not found with code: " + code));
        return convertToResponse(discount);
    }

    /**
     * Ottieni tutti gli sconti attivi
     */
    @Transactional(readOnly = true)
    public List<DiscountResponse> getActiveDiscounts() {
        return discountRepository.findAllActive()
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Ottieni sconti validi al momento
     */
    @Transactional(readOnly = true)
    public List<DiscountResponse> getValidDiscounts() {
        return discountRepository.findValidDiscounts(LocalDateTime.now())
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Applica sconto a un prodotto
     */
    public void applyDiscountToProduct(Long discountId, Long productId) {
        log.info("Applying discount {} to product {}", discountId, productId);

        Discount discount = discountRepository.findById(discountId)
            .orElseThrow(() -> new ResourceNotFoundException("Discount not found with id: " + discountId));

        Product product = productRepository.findActiveById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        discount.getProducts().add(product);
        product.getDiscounts().add(discount);

        discountRepository.save(discount);
        log.info("Discount applied successfully to product");
    }

    /**
     * Rimuovi sconto da un prodotto
     */
    public void removeDiscountFromProduct(Long discountId, Long productId) {
        log.info("Removing discount {} from product {}", discountId, productId);

        Discount discount = discountRepository.findById(discountId)
            .orElseThrow(() -> new ResourceNotFoundException("Discount not found with id: " + discountId));

        Product product = productRepository.findActiveById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        discount.getProducts().remove(product);
        product.getDiscounts().remove(discount);

        discountRepository.save(discount);
        log.info("Discount removed successfully from product");
    }

    /**
     * Valida se uno sconto è utilizzabile
     */
    @Transactional(readOnly = true)
    public boolean validateDiscount(String code) {
        Discount discount = discountRepository.findActiveByCode(code)
            .orElse(null);

        if (discount == null) {
            return false;
        }

        return discount.isValid();
    }

    /**
     * Valida se uno sconto è applicabile a un importo
     */
    @Transactional(readOnly = true)
    public boolean validateDiscountForAmount(String code, Integer purchaseAmountCents) {
        Discount discount = discountRepository.findActiveByCode(code)
            .orElse(null);

        if (discount == null || !discount.isValid()) {
            return false;
        }

        // Verifica importo minimo
        if (discount.getMinPurchaseAmountCents() != null
            && purchaseAmountCents < discount.getMinPurchaseAmountCents()) {
            return false;
        }

        return true;
    }

    /**
     * Incrementa utilizzo sconto
     */
    public void incrementUsage(Long discountId) {
        log.info("Incrementing usage for discount {}", discountId);

        Discount discount = discountRepository.findById(discountId)
            .orElseThrow(() -> new ResourceNotFoundException("Discount not found with id: " + discountId));

        if (discount.hasReachedMaxUses()) {
            throw new BadRequestException("Discount has reached maximum uses");
        }

        discount.incrementUsage();
        discountRepository.save(discount);

        log.info("Discount usage incremented. Current uses: {}", discount.getCurrentUses());
    }

    /**
     * Disattiva sconti scaduti (batch job)
     */
    public int deactivateExpiredDiscounts() {
        log.info("Deactivating expired discounts");
        int deactivated = discountRepository.deactivateExpiredDiscounts(LocalDateTime.now());
        log.info("Deactivated {} expired discounts", deactivated);
        return deactivated;
    }

    /**
     * Elimina sconto
     */
    public void deleteDiscount(Long id) {
        log.info("Deleting discount with id: {}", id);

        Discount discount = discountRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Discount not found with id: " + id));

        // Verifica che non ci siano prodotti associati attivi
        long productCount = discountRepository.countProductsByDiscountId(id);
        if (productCount > 0) {
            throw new BadRequestException("Cannot delete discount with associated products. Product count: " + productCount);
        }

        discountRepository.delete(discount);
        log.info("Discount deleted successfully with id: {}", id);
    }

    // ========== HELPER METHODS ==========

    /**
     * Converte Discount entity in DiscountResponse DTO
     */
    private DiscountResponse convertToResponse(Discount discount) {
        LocalDateTime now = LocalDateTime.now();
        boolean isValid = discount.isValid();
        boolean isExpired = discount.getEndsAt().isBefore(now);
        Integer remainingUses = discount.getMaxUses() != null
            ? discount.getMaxUses() - discount.getCurrentUses()
            : null;

        return DiscountResponse.builder()
            .id(discount.getId())
            .name(discount.getName())
            .code(discount.getCode())
            .description(discount.getDescription())
            .type(discount.getType())
            .value(discount.getValue())
            .startsAt(discount.getStartsAt())
            .endsAt(discount.getEndsAt())
            .maxUses(discount.getMaxUses())
            .maxUsesPerUser(discount.getMaxUsesPerUser())
            .currentUses(discount.getCurrentUses())
            .minPurchaseAmountCents(discount.getMinPurchaseAmountCents())
            .isActive(discount.getIsActive())
            .createdAt(discount.getCreatedAt())
            .updatedAt(discount.getUpdatedAt())
            .isValid(isValid)
            .isExpired(isExpired)
            .remainingUses(remainingUses)
            .build();
    }
}
