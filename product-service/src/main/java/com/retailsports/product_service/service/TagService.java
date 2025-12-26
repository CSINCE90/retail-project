package com.retailsports.product_service.service;

import com.retailsports.product_service.dto.request.TagRequest;
import com.retailsports.product_service.dto.response.TagResponse;
import com.retailsports.product_service.exception.BadRequestException;
import com.retailsports.product_service.exception.DuplicateResourceException;
import com.retailsports.product_service.exception.ResourceNotFoundException;
import com.retailsports.product_service.model.Tag;
import com.retailsports.product_service.model.Product;
import com.retailsports.product_service.repository.TagRepository;
import com.retailsports.product_service.repository.ProductRepository;
import com.retailsports.product_service.util.SlugUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TagService {

    private final TagRepository tagRepository;
    private final ProductRepository productRepository;

    /**
     * Crea un nuovo tag
     */
    public TagResponse createTag(TagRequest request) {
        log.info("Creating tag with name: {}", request.getName());

        // Validazione e generazione slug
        String slug = request.getSlug() != null && !request.getSlug().isBlank()
            ? request.getSlug()
            : SlugUtil.generateSlug(request.getName());

        if (tagRepository.existsBySlug(slug)) {
            throw new DuplicateResourceException("Tag with slug '" + slug + "' already exists");
        }

        Tag tag = Tag.builder()
            .name(request.getName())
            .slug(slug)
            .build();

        Tag saved = tagRepository.save(tag);
        log.info("Tag created successfully with id: {}", saved.getId());

        return convertToResponse(saved);
    }

    /**
     * Aggiorna tag esistente
     */
    public TagResponse updateTag(Long id, TagRequest request) {
        log.info("Updating tag with id: {}", id);

        Tag tag = tagRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Tag not found with id: " + id));

        // Validazione slug (se cambiato)
        String slug = request.getSlug() != null && !request.getSlug().isBlank()
            ? request.getSlug()
            : SlugUtil.generateSlug(request.getName());

        if (!tag.getSlug().equals(slug) && tagRepository.existsBySlug(slug)) {
            throw new DuplicateResourceException("Tag with slug '" + slug + "' already exists");
        }

        tag.setName(request.getName());
        tag.setSlug(slug);

        Tag updated = tagRepository.save(tag);
        log.info("Tag updated successfully with id: {}", updated.getId());

        return convertToResponse(updated);
    }

    /**
     * Ottieni tag per ID
     */
    @Transactional(readOnly = true)
    public TagResponse getTagById(Long id) {
        Tag tag = tagRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Tag not found with id: " + id));
        return convertToResponse(tag);
    }

    /**
     * Ottieni tag per slug
     */
    @Transactional(readOnly = true)
    public TagResponse getTagBySlug(String slug) {
        Tag tag = tagRepository.findBySlug(slug)
            .orElseThrow(() -> new ResourceNotFoundException("Tag not found with slug: " + slug));
        return convertToResponse(tag);
    }

    /**
     * Ottieni tutti i tag
     */
    @Transactional(readOnly = true)
    public List<TagResponse> getAllTags() {
        return tagRepository.findAllOrderedByName()
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Ottieni tag per prodotto
     */
    @Transactional(readOnly = true)
    public List<TagResponse> getTagsByProduct(Long productId) {
        productRepository.findActiveById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        return tagRepository.findByProductId(productId)
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Associa tag a prodotto
     */
    public void addTagToProduct(Long tagId, Long productId) {
        log.info("Adding tag {} to product {}", tagId, productId);

        Tag tag = tagRepository.findById(tagId)
            .orElseThrow(() -> new ResourceNotFoundException("Tag not found with id: " + tagId));

        Product product = productRepository.findActiveById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        tag.getProducts().add(product);
        product.getTags().add(tag);

        tagRepository.save(tag);
        log.info("Tag added successfully to product");
    }

    /**
     * Rimuovi tag da prodotto
     */
    public void removeTagFromProduct(Long tagId, Long productId) {
        log.info("Removing tag {} from product {}", tagId, productId);

        Tag tag = tagRepository.findById(tagId)
            .orElseThrow(() -> new ResourceNotFoundException("Tag not found with id: " + tagId));

        Product product = productRepository.findActiveById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        tag.getProducts().remove(product);
        product.getTags().remove(tag);

        tagRepository.save(tag);
        log.info("Tag removed successfully from product");
    }

    /**
     * Elimina tag
     */
    public void deleteTag(Long id) {
        log.info("Deleting tag with id: {}", id);

        Tag tag = tagRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Tag not found with id: " + id));

        // Verifica che non ci siano prodotti associati
        long productCount = tagRepository.countProductsByTagId(id);
        if (productCount > 0) {
            throw new BadRequestException("Cannot delete tag with associated products. Product count: " + productCount);
        }

        tagRepository.delete(tag);
        log.info("Tag deleted successfully with id: {}", id);
    }

    // ========== HELPER METHODS ==========

    private TagResponse convertToResponse(Tag tag) {
        Long productCount = tagRepository.countProductsByTagId(tag.getId());

        return TagResponse.builder()
            .id(tag.getId())
            .name(tag.getName())
            .slug(tag.getSlug())
            .createdAt(tag.getCreatedAt())
            .productCount(productCount)
            .build();
    }
}
