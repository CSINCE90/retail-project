package com.retailsports.product_service.repository;

import com.retailsports.product_service.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    // Trova tag per slug
    @Query("SELECT t FROM Tag t WHERE t.slug = :slug")
    Optional<Tag> findBySlug(@Param("slug") String slug);

    // Trova tag per nome
    @Query("SELECT t FROM Tag t WHERE t.name = :name")
    Optional<Tag> findByName(@Param("name") String name);

    // Trova tutti i tag ordinati per nome
    @Query("SELECT t FROM Tag t ORDER BY t.name ASC")
    List<Tag> findAllOrderedByName();

    // Trova tag per prodotto
    @Query("SELECT t FROM Tag t JOIN t.products p WHERE p.id = :productId ORDER BY t.name ASC")
    List<Tag> findByProductId(@Param("productId") Long productId);

    // Cerca tag per nome (like - case insensitive)
    @Query("SELECT t FROM Tag t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Tag> searchByName(@Param("search") String search);

    // Verifica se slug esiste
    boolean existsBySlug(String slug);

    // Verifica se nome esiste
    boolean existsByName(String name);

    // Conta prodotti associati a un tag
    @Query("SELECT COUNT(p) FROM Tag t JOIN t.products p WHERE t.id = :tagId")
    long countProductsByTagId(@Param("tagId") Long tagId);

    // Trova tag più usati (top N per numero di prodotti)
    @Query("SELECT t FROM Tag t JOIN t.products p GROUP BY t.id ORDER BY COUNT(p) DESC")
    List<Tag> findMostUsedTags();
}
