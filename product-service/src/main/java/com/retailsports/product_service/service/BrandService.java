package com.retailsports.product_service.service;

import com.retailsports.product_service.dto.request.BrandRequest;
import com.retailsports.product_service.dto.response.BrandResponse;
import com.retailsports.product_service.exception.BadRequestException;
import com.retailsports.product_service.exception.DuplicateResourceException;
import com.retailsports.product_service.exception.ResourceNotFoundException;
import com.retailsports.product_service.model.Brand;
import com.retailsports.product_service.repository.BrandRepository;
import com.retailsports.product_service.repository.ProductRepository;
import com.retailsports.product_service.util.SlugUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BrandService {

    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;

    /**
     * Crea un nuovo brand
     */
    public BrandResponse createBrand(BrandRequest request) {
        log.info("Creating brand with name: {}", request.getName());

        // Validazione e generazione slug
        String slug = request.getSlug() != null && !request.getSlug().isBlank()
            ? request.getSlug()
            : SlugUtil.generateSlug(request.getName());

        if (brandRepository.existsBySlug(slug)) {
            throw new DuplicateResourceException("Brand with slug '" + slug + "' already exists");
        }

        // Creazione brand
        Brand brand = Brand.builder()
            .name(request.getName())
            .slug(slug)
            .description(request.getDescription())
            .logoUrl(request.getLogoUrl())
            .websiteUrl(request.getWebsiteUrl())
            .isActive(request.getIsActive())
            .metaTitle(request.getMetaTitle())
            .metaDescription(request.getMetaDescription())
            .build();

        Brand saved = brandRepository.save(brand);
        log.info("Brand created successfully with id: {}", saved.getId());

        return convertToResponse(saved);
    }

    /**
     * Aggiorna un brand esistente
     */
    public BrandResponse updateBrand(Long id, BrandRequest request) {
        log.info("Updating brand with id: {}", id);

        Brand brand = brandRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + id));

        // Validazione slug (se cambiato)
        String slug = request.getSlug() != null && !request.getSlug().isBlank()
            ? request.getSlug()
            : SlugUtil.generateSlug(request.getName());

        if (!brand.getSlug().equals(slug) && brandRepository.existsBySlug(slug)) {
            throw new DuplicateResourceException("Brand with slug '" + slug + "' already exists");
        }

        // Aggiornamento campi
        brand.setName(request.getName());
        brand.setSlug(slug);
        brand.setDescription(request.getDescription());
        brand.setLogoUrl(request.getLogoUrl());
        brand.setWebsiteUrl(request.getWebsiteUrl());
        brand.setIsActive(request.getIsActive());
        brand.setMetaTitle(request.getMetaTitle());
        brand.setMetaDescription(request.getMetaDescription());

        Brand updated = brandRepository.save(brand);
        log.info("Brand updated successfully with id: {}", updated.getId());

        return convertToResponse(updated);
    }

    /**
     * Ottieni brand per ID
     */
    @Transactional(readOnly = true)
    public BrandResponse getBrandById(Long id) {
        Brand brand = brandRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + id));
        return convertToResponse(brand);
    }

    /**
     * Ottieni brand per slug
     */
    @Transactional(readOnly = true)
    public BrandResponse getBrandBySlug(String slug) {
        Brand brand = brandRepository.findBySlug(slug)
            .orElseThrow(() -> new ResourceNotFoundException("Brand not found with slug: " + slug));
        return convertToResponse(brand);
    }

    /**
     * Ottieni tutti i brand
     */
    @Transactional(readOnly = true)
    public List<BrandResponse> getAllBrands() {
        return brandRepository.findAllOrderedByName()
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Ottieni solo i brand attivi
     */
    @Transactional(readOnly = true)
    public List<BrandResponse> getActiveBrands() {
        return brandRepository.findAllActive()
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Elimina brand (solo se non ha prodotti associati)
     */
    public void deleteBrand(Long id) {
        log.info("Deleting brand with id: {}", id);

        Brand brand = brandRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + id));

        // Verifica che non ci siano prodotti associati
        long productCount = productRepository.countByBrandId(id);
        if (productCount > 0) {
            throw new BadRequestException("Cannot delete brand with associated products. Product count: " + productCount);
        }

        brandRepository.delete(brand);
        log.info("Brand deleted successfully with id: {}", id);
    }

    /**
     * Attiva/Disattiva brand
     */
    public BrandResponse toggleActiveStatus(Long id) {
        log.info("Toggling active status for brand with id: {}", id);

        Brand brand = brandRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + id));

        brand.setIsActive(!brand.getIsActive());
        Brand updated = brandRepository.save(brand);

        log.info("Brand active status toggled to: {} for id: {}", updated.getIsActive(), id);
        return convertToResponse(updated);
    }

    // ========== HELPER METHODS ==========

    /**
     * Converte Brand entity in BrandResponse DTO
     */
    private BrandResponse convertToResponse(Brand brand) {
        Long productCount = productRepository.countByBrandId(brand.getId());

        return BrandResponse.builder()
            .id(brand.getId())
            .name(brand.getName())
            .slug(brand.getSlug())
            .description(brand.getDescription())
            .logoUrl(brand.getLogoUrl())
            .websiteUrl(brand.getWebsiteUrl())
            .isActive(brand.getIsActive())
            .metaTitle(brand.getMetaTitle())
            .metaDescription(brand.getMetaDescription())
            .createdAt(brand.getCreatedAt())
            .updatedAt(brand.getUpdatedAt())
            .productCount(productCount)
            .build();
    }
}
