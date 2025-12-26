package com.retailsports.product_service.repository;

import com.retailsports.product_service.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    // Trova tutte le immagini di un prodotto ordinate per displayOrder
    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.id = :productId ORDER BY pi.displayOrder ASC")
    List<ProductImage> findByProductId(@Param("productId") Long productId);

    // Trova l'immagine principale di un prodotto
    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.id = :productId AND pi.isPrimary = true")
    Optional<ProductImage> findPrimaryImageByProductId(@Param("productId") Long productId);

    // Trova tutte le immagini secondarie di un prodotto
    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.id = :productId AND pi.isPrimary = false " +
           "ORDER BY pi.displayOrder ASC")
    List<ProductImage> findSecondaryImagesByProductId(@Param("productId") Long productId);

    // Conta immagini di un prodotto
    @Query("SELECT COUNT(pi) FROM ProductImage pi WHERE pi.product.id = :productId")
    long countByProductId(@Param("productId") Long productId);

    // Verifica se il prodotto ha un'immagine principale
    @Query("SELECT CASE WHEN COUNT(pi) > 0 THEN true ELSE false END FROM ProductImage pi " +
           "WHERE pi.product.id = :productId AND pi.isPrimary = true")
    boolean hasPrimaryImage(@Param("productId") Long productId);

    // Rimuovi il flag primary da tutte le immagini di un prodotto
    @Modifying
    @Query("UPDATE ProductImage pi SET pi.isPrimary = false WHERE pi.product.id = :productId")
    void removePrimaryFlagForProduct(@Param("productId") Long productId);

    // Verifica se l'immagine appartiene al prodotto
    @Query("SELECT CASE WHEN COUNT(pi) > 0 THEN true ELSE false END FROM ProductImage pi " +
           "WHERE pi.id = :imageId AND pi.product.id = :productId")
    boolean existsByIdAndProductId(@Param("imageId") Long imageId, @Param("productId") Long productId);

    // Elimina tutte le immagini di un prodotto
    @Modifying
    @Query("DELETE FROM ProductImage pi WHERE pi.product.id = :productId")
    void deleteByProductId(@Param("productId") Long productId);
}
