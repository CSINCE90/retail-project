package com.retailsports.user_service.repository;

import com.retailsports.user_service.model.Address;
import com.retailsports.user_service.model.Address.AddressType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    // Trova tutti gli indirizzi di un utente
    @Query("SELECT a FROM Address a WHERE a.user.id = :userId")
    List<Address> findByUserId(@Param("userId") Long userId);

    // Trova indirizzi per tipo (SHIPPING o BILLING)
    @Query("SELECT a FROM Address a WHERE a.user.id = :userId AND a.type = :type")
    List<Address> findByUserIdAndType(@Param("userId") Long userId, @Param("type") AddressType type);

    // Trova indirizzo default dell'utente
    @Query("SELECT a FROM Address a WHERE a.user.id = :userId AND a.isDefault = true")
    Optional<Address> findDefaultAddressByUserId(@Param("userId") Long userId);

    // Trova indirizzo default per tipo
    @Query("SELECT a FROM Address a WHERE a.user.id = :userId AND a.type = :type AND a.isDefault = true")
    Optional<Address> findDefaultAddressByUserIdAndType(@Param("userId") Long userId, @Param("type") AddressType type);

    // Rimuovi il flag default da tutti gli indirizzi di un utente (per un tipo)
    @Modifying
    @Query("UPDATE Address a SET a.isDefault = false WHERE a.user.id = :userId AND a.type = :type")
    void removeDefaultFlagForUserAndType(@Param("userId") Long userId, @Param("type") AddressType type);

    // Conta indirizzi di un utente
    @Query("SELECT COUNT(a) FROM Address a WHERE a.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    // Verifica se l'indirizzo appartiene all'utente
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Address a WHERE a.id = :addressId AND a.user.id = :userId")
    boolean existsByIdAndUserId(@Param("addressId") Long addressId, @Param("userId") Long userId);
}
