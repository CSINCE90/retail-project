package com.retailsports.product_service.repository;

import com.retailsports.product_service.model.ProductAttribute;
import com.retailsports.product_service.model.ProductAttribute.AttributeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, Long> {

    // Trova attributo per nome
    @Query("SELECT pa FROM ProductAttribute pa WHERE pa.name = :name")
    Optional<ProductAttribute> findByName(@Param("name") String name);

    // Trova attributi per tipo
    @Query("SELECT pa FROM ProductAttribute pa WHERE pa.type = :type ORDER BY pa.displayOrder ASC")
    List<ProductAttribute> findByType(@Param("type") AttributeType type);

    // Trova tutti gli attributi ordinati per displayOrder
    @Query("SELECT pa FROM ProductAttribute pa ORDER BY pa.displayOrder ASC")
    List<ProductAttribute> findAllOrderedByDisplayOrder();

    // Verifica se nome esiste
    boolean existsByName(String name);

    // Conta attributi per tipo
    @Query("SELECT COUNT(pa) FROM ProductAttribute pa WHERE pa.type = :type")
    long countByType(@Param("type") AttributeType type);
}
