package com.retailsports.cart_service.repository;

import com.retailsports.cart_service.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository per l'entità CartItem
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /**
     * Trova tutti gli items di un carrello
     */
    List<CartItem> findByCartId(Long cartId);

    /**
     * Trova un item specifico per cart e product
     */
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

    /**
     * Elimina un item per cart e product
     */
    void deleteByCartIdAndProductId(Long cartId, Long productId);

    /**
     * Elimina tutti gli items di un carrello
     */
    void deleteByCartId(Long cartId);
}
