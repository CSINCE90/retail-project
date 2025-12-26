package com.retailsports.product_service.repository;

import com.retailsports.product_service.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // ========== QUERY BASE ==========

    // Trova prodotto per SKU (solo attivi - no soft deleted)
    @Query("SELECT p FROM Product p WHERE p.sku = :sku AND p.deletedAt IS NULL")
    Optional<Product> findBySku(@Param("sku") String sku);

    // Trova prodotto per slug (solo attivi)
    @Query("SELECT p FROM Product p WHERE p.slug = :slug AND p.deletedAt IS NULL")
    Optional<Product> findBySlug(@Param("slug") String slug);

    // Trova prodotto per barcode (solo attivi)
    @Query("SELECT p FROM Product p WHERE p.barcode = :barcode AND p.deletedAt IS NULL")
    Optional<Product> findByBarcode(@Param("barcode") String barcode);

    // Override del findById per escludere soft deleted
    @Query("SELECT p FROM Product p WHERE p.id = :id AND p.deletedAt IS NULL")
    Optional<Product> findActiveById(@Param("id") Long id);

    // Trova tutti i prodotti attivi (no soft deleted)
    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL AND p.isActive = true ORDER BY p.createdAt DESC")
    List<Product> findAllActive();

    // Trova tutti i prodotti attivi con paginazione
    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL AND p.isActive = true")
    Page<Product> findAllActive(Pageable pageable);

    // Trova tutti i prodotti soft deleted
    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NOT NULL")
    List<Product> findAllDeleted();

    // ========== QUERY PER CATEGORIA ==========

    // Trova prodotti per categoria (solo attivi)
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.deletedAt IS NULL AND p.isActive = true")
    List<Product> findByCategoryId(@Param("categoryId") Long categoryId);

    // Trova prodotti per categoria con paginazione
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.deletedAt IS NULL AND p.isActive = true")
    Page<Product> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    // Trova prodotti per categoria slug
    @Query("SELECT p FROM Product p WHERE p.category.slug = :categorySlug AND p.deletedAt IS NULL AND p.isActive = true")
    List<Product> findByCategorySlug(@Param("categorySlug") String categorySlug);

    // Conta prodotti per categoria
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId AND p.deletedAt IS NULL AND p.isActive = true")
    long countByCategoryId(@Param("categoryId") Long categoryId);

    // ========== QUERY PER BRAND ==========

    // Trova prodotti per brand (solo attivi)
    @Query("SELECT p FROM Product p WHERE p.brand.id = :brandId AND p.deletedAt IS NULL AND p.isActive = true")
    List<Product> findByBrandId(@Param("brandId") Long brandId);

    // Trova prodotti per brand con paginazione
    @Query("SELECT p FROM Product p WHERE p.brand.id = :brandId AND p.deletedAt IS NULL AND p.isActive = true")
    Page<Product> findByBrandId(@Param("brandId") Long brandId, Pageable pageable);

    // Trova prodotti per brand slug
    @Query("SELECT p FROM Product p WHERE p.brand.slug = :brandSlug AND p.deletedAt IS NULL AND p.isActive = true")
    List<Product> findByBrandSlug(@Param("brandSlug") String brandSlug);

    // Conta prodotti per brand
    @Query("SELECT COUNT(p) FROM Product p WHERE p.brand.id = :brandId AND p.deletedAt IS NULL AND p.isActive = true")
    long countByBrandId(@Param("brandId") Long brandId);

    // ========== QUERY PER CATEGORIA E BRAND ==========

    // Trova prodotti per categoria e brand
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.brand.id = :brandId " +
           "AND p.deletedAt IS NULL AND p.isActive = true")
    List<Product> findByCategoryIdAndBrandId(@Param("categoryId") Long categoryId, @Param("brandId") Long brandId);

    // ========== QUERY PER PREZZO ==========

    // Trova prodotti in range di prezzo
    @Query("SELECT p FROM Product p WHERE p.priceCents BETWEEN :minPrice AND :maxPrice " +
           "AND p.deletedAt IS NULL AND p.isActive = true ORDER BY p.priceCents ASC")
    List<Product> findByPriceRange(@Param("minPrice") Integer minPrice, @Param("maxPrice") Integer maxPrice);

    // Trova prodotti in range di prezzo con paginazione
    @Query("SELECT p FROM Product p WHERE p.priceCents BETWEEN :minPrice AND :maxPrice " +
           "AND p.deletedAt IS NULL AND p.isActive = true")
    Page<Product> findByPriceRange(@Param("minPrice") Integer minPrice, @Param("maxPrice") Integer maxPrice, Pageable pageable);

    // ========== QUERY PER STATO PRODOTTO ==========

    // Trova prodotti in evidenza
    @Query("SELECT p FROM Product p WHERE p.isFeatured = true AND p.deletedAt IS NULL AND p.isActive = true " +
           "ORDER BY p.createdAt DESC")
    List<Product> findFeaturedProducts();

    // Trova prodotti nuovi
    @Query("SELECT p FROM Product p WHERE p.isNew = true AND p.deletedAt IS NULL AND p.isActive = true " +
           "ORDER BY p.createdAt DESC")
    List<Product> findNewProducts();

    // Trova prodotti in sconto
    @Query("SELECT p FROM Product p WHERE p.isOnSale = true AND p.deletedAt IS NULL AND p.isActive = true " +
           "ORDER BY p.createdAt DESC")
    List<Product> findProductsOnSale();

    // Trova prodotti in evidenza con paginazione
    @Query("SELECT p FROM Product p WHERE p.isFeatured = true AND p.deletedAt IS NULL AND p.isActive = true")
    Page<Product> findFeaturedProducts(Pageable pageable);

    // ========== QUERY PER INVENTARIO ==========

    // Trova prodotti con scorte basse
    @Query("SELECT p FROM Product p WHERE p.trackInventory = true AND p.stockQuantity <= p.lowStockThreshold " +
           "AND p.deletedAt IS NULL AND p.isActive = true ORDER BY p.stockQuantity ASC")
    List<Product> findLowStockProducts();

    // Trova prodotti esauriti
    @Query("SELECT p FROM Product p WHERE p.trackInventory = true AND p.stockQuantity = 0 " +
           "AND p.deletedAt IS NULL AND p.isActive = true")
    List<Product> findOutOfStockProducts();

    // Trova prodotti disponibili (stock > 0)
    @Query("SELECT p FROM Product p WHERE p.stockQuantity > 0 AND p.deletedAt IS NULL AND p.isActive = true")
    List<Product> findInStockProducts();

    // ========== QUERY DI RICERCA ==========

    // Ricerca full-text su nome e descrizione (like - case insensitive)
    @Query("SELECT p FROM Product p WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND p.deletedAt IS NULL AND p.isActive = true")
    List<Product> searchByNameOrDescription(@Param("search") String search);

    // Ricerca con paginazione
    @Query("SELECT p FROM Product p WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND p.deletedAt IS NULL AND p.isActive = true")
    Page<Product> searchByNameOrDescription(@Param("search") String search, Pageable pageable);

    // Ricerca avanzata (nome, descrizione, SKU)
    @Query("SELECT p FROM Product p WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND p.deletedAt IS NULL AND p.isActive = true")
    List<Product> advancedSearch(@Param("search") String search);

    // ========== QUERY PER STATISTICHE ==========

    // Trova prodotti più venduti (top N)
    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL AND p.isActive = true " +
           "ORDER BY p.salesCount DESC")
    List<Product> findTopSellingProducts(Pageable pageable);

    // Trova prodotti più visti (top N)
    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL AND p.isActive = true " +
           "ORDER BY p.viewsCount DESC")
    List<Product> findMostViewedProducts(Pageable pageable);

    // Trova prodotti meglio recensiti (top N)
    @Query("SELECT p FROM Product p WHERE p.ratingCount > 0 AND p.deletedAt IS NULL AND p.isActive = true " +
           "ORDER BY p.ratingAverage DESC, p.ratingCount DESC")
    List<Product> findTopRatedProducts(Pageable pageable);

    // ========== VERIFICHE ESISTENZA ==========

    // Verifica se SKU esiste (anche tra soft deleted)
    boolean existsBySku(String sku);

    // Verifica se slug esiste (anche tra soft deleted)
    boolean existsBySlug(String slug);

    // Verifica se barcode esiste (anche tra soft deleted)
    boolean existsByBarcode(String barcode);

    // ========== QUERY COMPLESSE ==========

    // Trova prodotti per categoria con filtri multipli
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId " +
           "AND (:brandId IS NULL OR p.brand.id = :brandId) " +
           "AND (:minPrice IS NULL OR p.priceCents >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.priceCents <= :maxPrice) " +
           "AND p.deletedAt IS NULL AND p.isActive = true")
    Page<Product> findWithFilters(
        @Param("categoryId") Long categoryId,
        @Param("brandId") Long brandId,
        @Param("minPrice") Integer minPrice,
        @Param("maxPrice") Integer maxPrice,
        Pageable pageable
    );

    // Conta prodotti attivi totali
    @Query("SELECT COUNT(p) FROM Product p WHERE p.deletedAt IS NULL AND p.isActive = true")
    long countActiveProducts();
}
