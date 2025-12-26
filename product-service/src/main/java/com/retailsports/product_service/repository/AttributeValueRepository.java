package com.retailsports.product_service.repository;

import com.retailsports.product_service.model.AttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttributeValueRepository extends JpaRepository<AttributeValue, Long> {

    // Trova tutti i valori per un attributo
    @Query("SELECT av FROM AttributeValue av WHERE av.attribute.id = :attributeId ORDER BY av.displayOrder ASC")
    List<AttributeValue> findByAttributeId(@Param("attributeId") Long attributeId);

    // Trova valore specifico per attributo e value
    @Query("SELECT av FROM AttributeValue av WHERE av.attribute.id = :attributeId AND av.value = :value")
    Optional<AttributeValue> findByAttributeIdAndValue(@Param("attributeId") Long attributeId, @Param("value") String value);

    // Trova valori per attributo per nome attributo
    @Query("SELECT av FROM AttributeValue av WHERE av.attribute.name = :attributeName ORDER BY av.displayOrder ASC")
    List<AttributeValue> findByAttributeName(@Param("attributeName") String attributeName);

    // Trova tutti i valori colore (con colorHex non null)
    @Query("SELECT av FROM AttributeValue av WHERE av.colorHex IS NOT NULL ORDER BY av.displayOrder ASC")
    List<AttributeValue> findAllColors();

    // Conta valori per attributo
    @Query("SELECT COUNT(av) FROM AttributeValue av WHERE av.attribute.id = :attributeId")
    long countByAttributeId(@Param("attributeId") Long attributeId);

    // Verifica se esiste combinazione attributo-valore
    @Query("SELECT CASE WHEN COUNT(av) > 0 THEN true ELSE false END FROM AttributeValue av " +
           "WHERE av.attribute.id = :attributeId AND av.value = :value")
    boolean existsByAttributeIdAndValue(@Param("attributeId") Long attributeId, @Param("value") String value);

    // Elimina tutti i valori di un attributo
    @Query("DELETE FROM AttributeValue av WHERE av.attribute.id = :attributeId")
    void deleteByAttributeId(@Param("attributeId") Long attributeId);
}
