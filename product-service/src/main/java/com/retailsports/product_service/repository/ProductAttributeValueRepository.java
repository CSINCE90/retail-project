package com.retailsports.product_service.repository;

import com.retailsports.product_service.model.ProductAttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductAttributeValueRepository extends JpaRepository<ProductAttributeValue, Long> {

    // Trova tutti gli attributi di un prodotto
    @Query("SELECT pav FROM ProductAttributeValue pav WHERE pav.product.id = :productId")
    List<ProductAttributeValue> findByProductId(@Param("productId") Long productId);

    // Trova prodotti con un determinato valore di attributo
    @Query("SELECT pav FROM ProductAttributeValue pav WHERE pav.attributeValue.id = :attributeValueId")
    List<ProductAttributeValue> findByAttributeValueId(@Param("attributeValueId") Long attributeValueId);

    // Trova attributi di un prodotto per tipo di attributo
    @Query("SELECT pav FROM ProductAttributeValue pav " +
           "WHERE pav.product.id = :productId AND pav.attributeValue.attribute.type = :attributeType")
    List<ProductAttributeValue> findByProductIdAndAttributeType(
        @Param("productId") Long productId,
        @Param("attributeType") com.retailsports.product_service.model.ProductAttribute.AttributeType attributeType
    );

    // Trova attributi di un prodotto per nome attributo (es. 'color', 'size')
    @Query("SELECT pav FROM ProductAttributeValue pav " +
           "WHERE pav.product.id = :productId AND pav.attributeValue.attribute.name = :attributeName")
    List<ProductAttributeValue> findByProductIdAndAttributeName(
        @Param("productId") Long productId,
        @Param("attributeName") String attributeName
    );

    // Trova associazione specifica prodotto-attributo
    @Query("SELECT pav FROM ProductAttributeValue pav " +
           "WHERE pav.product.id = :productId AND pav.attributeValue.id = :attributeValueId")
    Optional<ProductAttributeValue> findByProductIdAndAttributeValueId(
        @Param("productId") Long productId,
        @Param("attributeValueId") Long attributeValueId
    );

    // Verifica se esiste l'associazione
    @Query("SELECT CASE WHEN COUNT(pav) > 0 THEN true ELSE false END FROM ProductAttributeValue pav " +
           "WHERE pav.product.id = :productId AND pav.attributeValue.id = :attributeValueId")
    boolean existsByProductIdAndAttributeValueId(
        @Param("productId") Long productId,
        @Param("attributeValueId") Long attributeValueId
    );

    // Conta attributi di un prodotto
    @Query("SELECT COUNT(pav) FROM ProductAttributeValue pav WHERE pav.product.id = :productId")
    long countByProductId(@Param("productId") Long productId);

    // Conta prodotti con un determinato valore di attributo
    @Query("SELECT COUNT(pav) FROM ProductAttributeValue pav WHERE pav.attributeValue.id = :attributeValueId")
    long countByAttributeValueId(@Param("attributeValueId") Long attributeValueId);

    // Elimina tutti gli attributi di un prodotto
    @Modifying
    @Query("DELETE FROM ProductAttributeValue pav WHERE pav.product.id = :productId")
    void deleteByProductId(@Param("productId") Long productId);

    // Elimina tutte le associazioni con un attributo value specifico
    @Modifying
    @Query("DELETE FROM ProductAttributeValue pav WHERE pav.attributeValue.id = :attributeValueId")
    void deleteByAttributeValueId(@Param("attributeValueId") Long attributeValueId);

    // Elimina associazione specifica
    @Modifying
    @Query("DELETE FROM ProductAttributeValue pav " +
           "WHERE pav.product.id = :productId AND pav.attributeValue.id = :attributeValueId")
    void deleteByProductIdAndAttributeValueId(
        @Param("productId") Long productId,
        @Param("attributeValueId") Long attributeValueId
    );

    // Trova prodotti con combinazione di attributi (es. colore=Rosso AND taglia=M)
    @Query("SELECT pav.product.id FROM ProductAttributeValue pav " +
           "WHERE pav.attributeValue.id IN :attributeValueIds " +
           "GROUP BY pav.product.id " +
           "HAVING COUNT(DISTINCT pav.attributeValue.id) = :count")
    List<Long> findProductIdsByAttributeValues(
        @Param("attributeValueIds") List<Long> attributeValueIds,
        @Param("count") Long count
    );
}
