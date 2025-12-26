package com.retailsports.product_service.service;

import com.retailsports.product_service.dto.request.CategoryRequest;
import com.retailsports.product_service.dto.response.CategoryResponse;
import com.retailsports.product_service.exception.BadRequestException;
import com.retailsports.product_service.exception.DuplicateResourceException;
import com.retailsports.product_service.exception.ResourceNotFoundException;
import com.retailsports.product_service.model.Category;
import com.retailsports.product_service.repository.CategoryRepository;
import com.retailsports.product_service.repository.ProductRepository;
import com.retailsports.product_service.util.SlugUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    /**
     * Crea una nuova categoria
     */
    public CategoryResponse createCategory(CategoryRequest request) {
        log.info("Creating category with name: {}", request.getName());

        // Validazione slug
        String slug = request.getSlug() != null && !request.getSlug().isBlank()
            ? request.getSlug()
            : SlugUtil.generateSlug(request.getName());

        if (categoryRepository.existsBySlug(slug)) {
            throw new DuplicateResourceException("Category with slug '" + slug + "' already exists");
        }

        // Validazione parent (se fornito)
        Category parent = null;
        if (request.getParentId() != null) {
            parent = categoryRepository.findById(request.getParentId())
                .orElseThrow(() -> new ResourceNotFoundException("Parent category not found with id: " + request.getParentId()));
        }

        // Creazione categoria
        Category category = Category.builder()
            .name(request.getName())
            .slug(slug)
            .description(request.getDescription())
            .parent(parent)
            .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
            .isActive(request.getIsActive())
            .metaTitle(request.getMetaTitle())
            .metaDescription(request.getMetaDescription())
            .build();

        Category saved = categoryRepository.save(category);
        log.info("Category created successfully with id: {}", saved.getId());

        return convertToResponse(saved);
    }

    /**
     * Aggiorna una categoria esistente
     */
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        log.info("Updating category with id: {}", id);

        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        // Validazione slug (se cambiato)
        String slug = request.getSlug() != null && !request.getSlug().isBlank()
            ? request.getSlug()
            : SlugUtil.generateSlug(request.getName());

        if (!category.getSlug().equals(slug) && categoryRepository.existsBySlug(slug)) {
            throw new DuplicateResourceException("Category with slug '" + slug + "' already exists");
        }

        // Validazione parent (impedisce cicli nella gerarchia)
        if (request.getParentId() != null) {
            if (request.getParentId().equals(id)) {
                throw new BadRequestException("Category cannot be parent of itself");
            }

            Category parent = categoryRepository.findById(request.getParentId())
                .orElseThrow(() -> new ResourceNotFoundException("Parent category not found with id: " + request.getParentId()));

            // Verifica che il nuovo parent non sia un discendente della categoria corrente
            if (isDescendant(id, parent.getId())) {
                throw new BadRequestException("Cannot set a descendant category as parent");
            }

            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        // Aggiornamento campi
        category.setName(request.getName());
        category.setSlug(slug);
        category.setDescription(request.getDescription());
        category.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        category.setIsActive(request.getIsActive());
        category.setMetaTitle(request.getMetaTitle());
        category.setMetaDescription(request.getMetaDescription());

        Category updated = categoryRepository.save(category);
        log.info("Category updated successfully with id: {}", updated.getId());

        return convertToResponse(updated);
    }

    /**
     * Ottieni categoria per ID
     */
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return convertToResponse(category);
    }

    /**
     * Ottieni categoria per slug
     */
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found with slug: " + slug));
        return convertToResponse(category);
    }

    /**
     * Ottieni tutte le categorie
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAllOrderedByDisplayOrder()
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Ottieni categorie root (senza parent)
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> getRootCategories() {
        return categoryRepository.findAllActiveRootCategories()
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Ottieni sottocategorie per parent ID
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> getSubcategories(Long parentId) {
        // Verifica che il parent esista
        categoryRepository.findById(parentId)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + parentId));

        return categoryRepository.findActiveByParentId(parentId)
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Ottieni albero completo delle categorie (con gerarchia)
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoryTree() {
        List<Category> rootCategories = categoryRepository.findAllActiveRootCategories();
        return rootCategories.stream()
            .map(this::convertToResponseWithChildren)
            .collect(Collectors.toList());
    }

    /**
     * Elimina categoria (solo se non ha prodotti associati)
     */
    public void deleteCategory(Long id) {
        log.info("Deleting category with id: {}", id);

        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        // Verifica che non ci siano prodotti associati
        long productCount = productRepository.countByCategoryId(id);
        if (productCount > 0) {
            throw new BadRequestException("Cannot delete category with associated products. Product count: " + productCount);
        }

        // Verifica che non ci siano sottocategorie
        long subcategoryCount = categoryRepository.countByParentId(id);
        if (subcategoryCount > 0) {
            throw new BadRequestException("Cannot delete category with subcategories. Subcategory count: " + subcategoryCount);
        }

        categoryRepository.delete(category);
        log.info("Category deleted successfully with id: {}", id);
    }

    /**
     * Attiva/Disattiva categoria
     */
    public CategoryResponse toggleActiveStatus(Long id) {
        log.info("Toggling active status for category with id: {}", id);

        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        category.setIsActive(!category.getIsActive());
        Category updated = categoryRepository.save(category);

        log.info("Category active status toggled to: {} for id: {}", updated.getIsActive(), id);
        return convertToResponse(updated);
    }

    // ========== HELPER METHODS ==========

    /**
     * Verifica se categoryId è un discendente di potentialAncestorId
     */
    private boolean isDescendant(Long categoryId, Long potentialAncestorId) {
        Category current = categoryRepository.findById(potentialAncestorId).orElse(null);
        while (current != null && current.getParent() != null) {
            if (current.getParent().getId().equals(categoryId)) {
                return true;
            }
            current = current.getParent();
        }
        return false;
    }

    /**
     * Converte Category entity in CategoryResponse DTO
     */
    private CategoryResponse convertToResponse(Category category) {
        Long productCount = productRepository.countByCategoryId(category.getId());

        return CategoryResponse.builder()
            .id(category.getId())
            .name(category.getName())
            .slug(category.getSlug())
            .description(category.getDescription())
            .parentId(category.getParent() != null ? category.getParent().getId() : null)
            .displayOrder(category.getDisplayOrder())
            .isActive(category.getIsActive())
            .metaTitle(category.getMetaTitle())
            .metaDescription(category.getMetaDescription())
            .createdAt(category.getCreatedAt())
            .updatedAt(category.getUpdatedAt())
            .productCount(productCount)
            .build();
    }

    /**
     * Converte Category entity in CategoryResponse DTO con sottocategorie (ricorsivo)
     */
    private CategoryResponse convertToResponseWithChildren(Category category) {
        CategoryResponse response = convertToResponse(category);

        // Carica ricorsivamente le sottocategorie
        List<CategoryResponse> subcategories = category.getChildren()
            .stream()
            .filter(Category::getIsActive)
            .map(this::convertToResponseWithChildren)
            .collect(Collectors.toList());

        response.setSubcategories(subcategories);
        return response;
    }
}
