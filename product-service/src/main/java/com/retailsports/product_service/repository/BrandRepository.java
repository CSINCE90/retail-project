package com.retailsports.product_service.repository;

import com.retailsports.product_service.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    // Trova brand per slug
    @Query("SELECT b FROM Brand b WHERE b.slug = :slug")
    Optional<Brand> findBySlug(@Param("slug") String slug);

    // Trova brand per nome
    @Query("SELECT b FROM Brand b WHERE b.name = :name")
    Optional<Brand> findByName(@Param("name") String name);

    // Trova tutti i brand attivi
    @Query("SELECT b FROM Brand b WHERE b.isActive = true ORDER BY b.name ASC")
    List<Brand> findAllActive();

    // Trova tutti i brand ordinati per nome
    @Query("SELECT b FROM Brand b ORDER BY b.name ASC")
    List<Brand> findAllOrderedByName();

    // Verifica se slug esiste
    boolean existsBySlug(String slug);

    // Verifica se nome esiste
    boolean existsByName(String name);

    // Cerca brand per nome o descrizione (like - case insensitive)
    @Query("SELECT b FROM Brand b WHERE LOWER(b.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(b.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Brand> searchByNameOrDescription(@Param("search") String search);

    // Cerca brand attivi per nome o descrizione
    @Query("SELECT b FROM Brand b WHERE (LOWER(b.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(b.description) LIKE LOWER(CONCAT('%', :search, '%'))) AND b.isActive = true")
    List<Brand> searchActiveByNameOrDescription(@Param("search") String search);
}
