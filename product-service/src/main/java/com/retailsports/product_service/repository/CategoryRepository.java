package com.retailsports.product_service.repository;

import com.retailsports.product_service.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Trova categoria per slug
    @Query("SELECT c FROM Category c WHERE c.slug = :slug")
    Optional<Category> findBySlug(@Param("slug") String slug);

    // Trova categoria per nome
    @Query("SELECT c FROM Category c WHERE c.name = :name")
    Optional<Category> findByName(@Param("name") String name);

    // Trova tutte le categorie attive
    @Query("SELECT c FROM Category c WHERE c.isActive = true ORDER BY c.displayOrder ASC")
    List<Category> findAllActive();

    // Trova tutte le categorie root (senza parent)
    @Query("SELECT c FROM Category c WHERE c.parent IS NULL ORDER BY c.displayOrder ASC")
    List<Category> findAllRootCategories();

    // Trova tutte le categorie root attive
    @Query("SELECT c FROM Category c WHERE c.parent IS NULL AND c.isActive = true ORDER BY c.displayOrder ASC")
    List<Category> findAllActiveRootCategories();

    // Trova sottocategorie per parent_id
    @Query("SELECT c FROM Category c WHERE c.parent.id = :parentId ORDER BY c.displayOrder ASC")
    List<Category> findByParentId(@Param("parentId") Long parentId);

    // Trova sottocategorie attive per parent_id
    @Query("SELECT c FROM Category c WHERE c.parent.id = :parentId AND c.isActive = true ORDER BY c.displayOrder ASC")
    List<Category> findActiveByParentId(@Param("parentId") Long parentId);

    // Conta sottocategorie per parent_id
    @Query("SELECT COUNT(c) FROM Category c WHERE c.parent.id = :parentId")
    long countByParentId(@Param("parentId") Long parentId);

    // Verifica se slug esiste
    boolean existsBySlug(String slug);

    // Verifica se nome esiste
    boolean existsByName(String name);

    // Cerca categorie per nome (like - case insensitive)
    @Query("SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Category> searchByNameOrDescription(@Param("search") String search);

    // Trova tutte le categorie ordinate per displayOrder
    @Query("SELECT c FROM Category c ORDER BY c.displayOrder ASC")
    List<Category> findAllOrderedByDisplayOrder();
}
