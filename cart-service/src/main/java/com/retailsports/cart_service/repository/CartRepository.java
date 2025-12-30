package com.retailsports.cart_service.repository;

import com.retailsports.cart_service.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository per l'entità Cart
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * Trova un carrello per userId
     */
    Optional<Cart> findByUserId(Long userId);

    /**
     * Verifica se esiste un carrello per un utente
     */
    boolean existsByUserId(Long userId);

    /**
     * Elimina un carrello per userId
     */
    void deleteByUserId(Long userId);
}
