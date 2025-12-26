package com.retailsports.product_service.service;

import com.retailsports.product_service.dto.request.ProductRequest;
import com.retailsports.product_service.dto.request.ProductSearchRequest;
import com.retailsports.product_service.dto.response.*;
import com.retailsports.product_service.exception.BadRequestException;
import com.retailsports.product_service.exception.DuplicateResourceException;
import com.retailsports.product_service.exception.ResourceNotFoundException;
import com.retailsports.product_service.model.*;
import com.retailsports.product_service.repository.*;
import com.retailsports.product_service.util.SlugUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductImageRepository productImageRepository;
    private final DiscountRepository discountRepository;
    private final ProductAttributeValueRepository productAttributeValueRepository;

    /**
     * Crea un nuovo prodotto
     */
    public ProductResponse createProduct(ProductRequest request) {
        log.info("Creating product with SKU: {}", request.getSku());

        // Validazione SKU univoco
        if (productRepository.existsBySku(request.getSku())) {
            throw new DuplicateResourceException("Product with SKU '" + request.getSku() + "' already exists");
        }

        // Validazione e generazione slug
        String slug = request.getSlug() != null && !request.getSlug().isBlank()
            ? request.getSlug()
            : SlugUtil.generateSlug(request.getName());

        if (productRepository.existsBySlug(slug)) {
            throw new DuplicateResourceException("Product with slug '" + slug + "' already exists");
        }

        // Validazione categoria (obbligatoria)
        Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        // Validazione brand (opzionale)
        Brand brand = null;
        if (request.getBrandId() != null) {
            brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + request.getBrandId()));
        }

        // Calcolo automatico isOnSale
        boolean isOnSale = request.getCompareAtPriceCents() != null
            && request.getCompareAtPriceCents() > request.getPriceCents();

        // Creazione prodotto
        Product product = Product.builder()
            .sku(request.getSku())
            .barcode(request.getBarcode())
            .name(request.getName())
            .slug(slug)
            .description(request.getDescription())
            .longDescription(request.getLongDescription())
            .category(category)
            .brand(brand)
            .priceCents(request.getPriceCents())
            .compareAtPriceCents(request.getCompareAtPriceCents())
            .costPriceCents(request.getCostPriceCents())
            .weightGrams(request.getWeightGrams())
            .lengthCm(request.getLengthCm())
            .widthCm(request.getWidthCm())
            .heightCm(request.getHeightCm())
            .stockQuantity(request.getStockQuantity())
            .lowStockThreshold(request.getLowStockThreshold() != null ? request.getLowStockThreshold() : 10)
            .trackInventory(request.getTrackInventory())
            .isActive(request.getIsActive())
            .isFeatured(request.getIsFeatured() != null ? request.getIsFeatured() : false)
            .isNew(request.getIsNew() != null ? request.getIsNew() : false)
            .isOnSale(isOnSale)
            .metaTitle(request.getMetaTitle())
            .metaDescription(request.getMetaDescription())
            .metaKeywords(request.getMetaKeywords())
            .build();

        Product saved = productRepository.save(product);
        log.info("Product created successfully with id: {}", saved.getId());

        return convertToFullResponse(saved);
    }

    /**
     * Aggiorna un prodotto esistente
     */
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        log.info("Updating product with id: {}", id);

        Product product = productRepository.findActiveById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        // Validazione SKU (se cambiato)
        if (!product.getSku().equals(request.getSku()) && productRepository.existsBySku(request.getSku())) {
            throw new DuplicateResourceException("Product with SKU '" + request.getSku() + "' already exists");
        }

        // Validazione slug (se cambiato)
        String slug = request.getSlug() != null && !request.getSlug().isBlank()
            ? request.getSlug()
            : SlugUtil.generateSlug(request.getName());

        if (!product.getSlug().equals(slug) && productRepository.existsBySlug(slug)) {
            throw new DuplicateResourceException("Product with slug '" + slug + "' already exists");
        }

        // Validazione categoria
        Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        // Validazione brand
        Brand brand = null;
        if (request.getBrandId() != null) {
            brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + request.getBrandId()));
        }

        // Calcolo automatico isOnSale
        boolean isOnSale = request.getCompareAtPriceCents() != null
            && request.getCompareAtPriceCents() > request.getPriceCents();

        // Aggiornamento campi
        product.setSku(request.getSku());
        product.setBarcode(request.getBarcode());
        product.setName(request.getName());
        product.setSlug(slug);
        product.setDescription(request.getDescription());
        product.setLongDescription(request.getLongDescription());
        product.setCategory(category);
        product.setBrand(brand);
        product.setPriceCents(request.getPriceCents());
        product.setCompareAtPriceCents(request.getCompareAtPriceCents());
        product.setCostPriceCents(request.getCostPriceCents());
        product.setWeightGrams(request.getWeightGrams());
        product.setLengthCm(request.getLengthCm());
        product.setWidthCm(request.getWidthCm());
        product.setHeightCm(request.getHeightCm());
        product.setStockQuantity(request.getStockQuantity());
        product.setLowStockThreshold(request.getLowStockThreshold() != null ? request.getLowStockThreshold() : 10);
        product.setTrackInventory(request.getTrackInventory());
        product.setIsActive(request.getIsActive());
        product.setIsFeatured(request.getIsFeatured() != null ? request.getIsFeatured() : false);
        product.setIsNew(request.getIsNew() != null ? request.getIsNew() : false);
        product.setIsOnSale(isOnSale);
        product.setMetaTitle(request.getMetaTitle());
        product.setMetaDescription(request.getMetaDescription());
        product.setMetaKeywords(request.getMetaKeywords());

        Product updated = productRepository.save(product);
        log.info("Product updated successfully with id: {}", updated.getId());

        return convertToFullResponse(updated);
    }

    /**
     * Ottieni prodotto per ID (completo)
     */
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findActiveById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        // Incrementa views in modo asincrono
        incrementViews(id);

        return convertToFullResponse(product);
    }

    /**
     * Ottieni prodotto per SKU
     */
    @Transactional(readOnly = true)
    public ProductResponse getProductBySku(String sku) {
        Product product = productRepository.findBySku(sku)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with SKU: " + sku));
        return convertToFullResponse(product);
    }

    /**
     * Ottieni prodotto per slug
     */
    @Transactional(readOnly = true)
    public ProductResponse getProductBySlug(String slug) {
        Product product = productRepository.findBySlug(slug)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with slug: " + slug));

        // Incrementa views
        incrementViews(product.getId());

        return convertToFullResponse(product);
    }

    /**
     * Ricerca prodotti con filtri avanzati
     */
    @Transactional(readOnly = true)
    public Page<ProductSummaryResponse> searchProducts(ProductSearchRequest request, Pageable pageable) {
        log.info("Searching products with filters: {}", request);

        Page<Product> products;

        // Se c'è una keyword, usa la ricerca full-text
        if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
            products = productRepository.searchByNameOrDescription(request.getKeyword(), pageable);
        }
        // Altrimenti usa i filtri
        else if (request.getCategoryId() != null) {
            products = productRepository.findWithFilters(
                request.getCategoryId(),
                request.getBrandId(),
                request.getMinPrice(),
                request.getMaxPrice(),
                pageable
            );
        }
        // Prodotti generici
        else {
            products = productRepository.findAllActive(pageable);
        }

        return products.map(this::convertToSummaryResponse);
    }

    /**
     * Ottieni prodotti per categoria
     */
    @Transactional(readOnly = true)
    public Page<ProductSummaryResponse> getProductsByCategory(Long categoryId, Pageable pageable) {
        // Verifica che la categoria esista
        categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));

        Page<Product> products = productRepository.findByCategoryId(categoryId, pageable);
        return products.map(this::convertToSummaryResponse);
    }

    /**
     * Ottieni prodotti per brand
     */
    @Transactional(readOnly = true)
    public Page<ProductSummaryResponse> getProductsByBrand(Long brandId, Pageable pageable) {
        // Verifica che il brand esista
        brandRepository.findById(brandId)
            .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + brandId));

        Page<Product> products = productRepository.findByBrandId(brandId, pageable);
        return products.map(this::convertToSummaryResponse);
    }

    /**
     * Ottieni prodotti in evidenza
     */
    @Transactional(readOnly = true)
    public Page<ProductSummaryResponse> getFeaturedProducts(Pageable pageable) {
        Page<Product> products = productRepository.findFeaturedProducts(pageable);
        return products.map(this::convertToSummaryResponse);
    }

    /**
     * Ottieni nuovi prodotti
     */
    @Transactional(readOnly = true)
    public Page<ProductSummaryResponse> getNewProducts(Pageable pageable) {
        Page<Product> products = productRepository.findAllActive(
            PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
            )
        );
        return products.map(this::convertToSummaryResponse);
    }

    /**
     * Ottieni prodotti in sconto
     */
    @Transactional(readOnly = true)
    public Page<ProductSummaryResponse> getProductsOnSale(Pageable pageable) {
        List<Product> products = productRepository.findProductsOnSale();

        // Converti in Page manualmente
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), products.size());

        List<ProductSummaryResponse> content = products.subList(start, end)
            .stream()
            .map(this::convertToSummaryResponse)
            .collect(Collectors.toList());

        return new org.springframework.data.domain.PageImpl<>(content, pageable, products.size());
    }

    /**
     * Soft delete prodotto
     */
    public void softDeleteProduct(Long id) {
        log.info("Soft deleting product with id: {}", id);

        Product product = productRepository.findActiveById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        product.softDelete();
        productRepository.save(product);

        log.info("Product soft deleted successfully with id: {}", id);
    }

    /**
     * Ripristina prodotto soft deleted
     */
    public ProductResponse restoreProduct(Long id) {
        log.info("Restoring product with id: {}", id);

        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (!product.isDeleted()) {
            throw new BadRequestException("Product is not deleted");
        }

        product.restore();
        Product restored = productRepository.save(product);

        log.info("Product restored successfully with id: {}", id);
        return convertToFullResponse(restored);
    }

    /**
     * Aggiorna stock prodotto
     */
    public ProductResponse updateStock(Long id, Integer quantity) {
        log.info("Updating stock for product id: {} with quantity: {}", id, quantity);

        Product product = productRepository.findActiveById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (!product.getTrackInventory()) {
            throw new BadRequestException("Product does not track inventory");
        }

        int newStock = product.getStockQuantity() + quantity;
        if (newStock < 0) {
            throw new BadRequestException("Insufficient stock. Available: " + product.getStockQuantity() + ", Requested: " + Math.abs(quantity));
        }

        product.setStockQuantity(newStock);
        Product updated = productRepository.save(product);

        log.info("Stock updated successfully for product id: {}. New stock: {}", id, newStock);
        return convertToFullResponse(updated);
    }

    /**
     * Incrementa views prodotto (asincrono)
     */
    public void incrementViews(Long id) {
        productRepository.findActiveById(id).ifPresent(product -> {
            product.incrementViews();
            productRepository.save(product);
        });
    }

    // ========== HELPER METHODS - MAPPERS ==========

    /**
     * Converte Product entity in ProductResponse completo (con tutte le relazioni)
     */
    private ProductResponse convertToFullResponse(Product product) {
        // Category
        CategoryResponse categoryResponse = CategoryResponse.builder()
            .id(product.getCategory().getId())
            .name(product.getCategory().getName())
            .slug(product.getCategory().getSlug())
            .build();

        // Brand
        BrandResponse brandResponse = null;
        if (product.getBrand() != null) {
            brandResponse = BrandResponse.builder()
                .id(product.getBrand().getId())
                .name(product.getBrand().getName())
                .slug(product.getBrand().getSlug())
                .logoUrl(product.getBrand().getLogoUrl())
                .build();
        }

        // Images
        List<ProductImage> images = productImageRepository.findByProductId(product.getId());
        List<ProductImageResponse> imageResponses = images.stream()
            .map(this::convertImageToResponse)
            .collect(Collectors.toList());

        ProductImageResponse primaryImage = images.stream()
            .filter(ProductImage::getIsPrimary)
            .findFirst()
            .map(this::convertImageToResponse)
            .orElse(null);

        // Active Discounts
        List<Discount> activeDiscounts = discountRepository.findValidDiscountsByProductId(product.getId(), LocalDateTime.now());
        List<DiscountResponse> discountResponses = activeDiscounts.stream()
            .map(this::convertDiscountToResponse)
            .collect(Collectors.toList());

        // Prezzi formattati
        String priceFormatted = formatPrice(product.getPriceCents());
        String compareAtPriceFormatted = product.getCompareAtPriceCents() != null
            ? formatPrice(product.getCompareAtPriceCents())
            : null;

        BigDecimal discountPercentage = calculateDiscountPercentage(product.getPriceCents(), product.getCompareAtPriceCents());

        return ProductResponse.builder()
            .id(product.getId())
            .sku(product.getSku())
            .barcode(product.getBarcode())
            .name(product.getName())
            .slug(product.getSlug())
            .description(product.getDescription())
            .longDescription(product.getLongDescription())
            .category(categoryResponse)
            .brand(brandResponse)
            .priceCents(product.getPriceCents())
            .compareAtPriceCents(product.getCompareAtPriceCents())
            .costPriceCents(product.getCostPriceCents())
            .priceFormatted(priceFormatted)
            .compareAtPriceFormatted(compareAtPriceFormatted)
            .discountPercentage(discountPercentage)
            .weightGrams(product.getWeightGrams())
            .lengthCm(product.getLengthCm())
            .widthCm(product.getWidthCm())
            .heightCm(product.getHeightCm())
            .stockQuantity(product.getStockQuantity())
            .lowStockThreshold(product.getLowStockThreshold())
            .trackInventory(product.getTrackInventory())
            .isLowStock(product.isLowStock())
            .isOutOfStock(product.getStockQuantity() == 0)
            .isActive(product.getIsActive())
            .isFeatured(product.getIsFeatured())
            .isNew(product.getIsNew())
            .isOnSale(product.getIsOnSale())
            .metaTitle(product.getMetaTitle())
            .metaDescription(product.getMetaDescription())
            .metaKeywords(product.getMetaKeywords())
            .viewsCount(product.getViewsCount())
            .salesCount(product.getSalesCount())
            .ratingAverage(product.getRatingAverage())
            .ratingCount(product.getRatingCount())
            .createdAt(product.getCreatedAt())
            .updatedAt(product.getUpdatedAt())
            .images(imageResponses)
            .primaryImage(primaryImage)
            .activeDiscounts(discountResponses)
            .build();
    }

    /**
     * Converte Product entity in ProductSummaryResponse (lightweight)
     */
    private ProductSummaryResponse convertToSummaryResponse(Product product) {
        // Primary image URL
        String primaryImageUrl = productImageRepository.findPrimaryImageByProductId(product.getId())
            .map(ProductImage::getImageUrl)
            .orElse(null);

        // Prezzo formattato
        String priceFormatted = formatPrice(product.getPriceCents());
        BigDecimal discountPercentage = calculateDiscountPercentage(product.getPriceCents(), product.getCompareAtPriceCents());

        // Badge
        String badge = determineBadge(product);

        return ProductSummaryResponse.builder()
            .id(product.getId())
            .sku(product.getSku())
            .name(product.getName())
            .slug(product.getSlug())
            .priceCents(product.getPriceCents())
            .compareAtPriceCents(product.getCompareAtPriceCents())
            .priceFormatted(priceFormatted)
            .discountPercentage(discountPercentage)
            .brandName(product.getBrand() != null ? product.getBrand().getName() : null)
            .categoryName(product.getCategory().getName())
            .primaryImageUrl(primaryImageUrl)
            .ratingAverage(product.getRatingAverage())
            .ratingCount(product.getRatingCount())
            .isFeatured(product.getIsFeatured())
            .isNew(product.getIsNew())
            .isOnSale(product.getIsOnSale())
            .isInStock(product.getStockQuantity() > 0)
            .badge(badge)
            .build();
    }

    private ProductImageResponse convertImageToResponse(ProductImage image) {
        return ProductImageResponse.builder()
            .id(image.getId())
            .imageUrl(image.getImageUrl())
            .altText(image.getAltText())
            .displayOrder(image.getDisplayOrder())
            .isPrimary(image.getIsPrimary())
            .createdAt(image.getCreatedAt())
            .build();
    }

    private DiscountResponse convertDiscountToResponse(Discount discount) {
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
            .type(discount.getType())
            .value(discount.getValue())
            .startsAt(discount.getStartsAt())
            .endsAt(discount.getEndsAt())
            .isValid(isValid)
            .isExpired(isExpired)
            .remainingUses(remainingUses)
            .build();
    }

    /**
     * Formatta prezzo in centesimi in stringa (es. 9999 -> "99.99€")
     */
    private String formatPrice(Integer priceCents) {
        if (priceCents == null) return null;
        BigDecimal price = BigDecimal.valueOf(priceCents).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return String.format("%.2f€", price);
    }

    /**
     * Calcola percentuale di sconto
     */
    private BigDecimal calculateDiscountPercentage(Integer priceCents, Integer compareAtPriceCents) {
        if (compareAtPriceCents == null || compareAtPriceCents <= priceCents) {
            return null;
        }
        BigDecimal price = BigDecimal.valueOf(priceCents);
        BigDecimal compareAt = BigDecimal.valueOf(compareAtPriceCents);
        BigDecimal discount = compareAt.subtract(price).divide(compareAt, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        return discount.setScale(0, RoundingMode.HALF_UP);
    }

    /**
     * Determina badge da mostrare sul prodotto
     */
    private String determineBadge(Product product) {
        if (product.getStockQuantity() == 0) return "OUT OF STOCK";
        if (product.getIsNew()) return "NEW";
        if (product.getIsOnSale()) return "SALE";
        if (product.getIsFeatured()) return "FEATURED";
        return null;
    }
}
