package com.retailsports.user_service.repository;

import com.retailsports.user_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Trova utente per username (solo attivi - no soft deleted)
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.deletedAt IS NULL")
    Optional<User> findByUsername(@Param("username") String username);

    // Trova utente per email (solo attivi)
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.deletedAt IS NULL")
    Optional<User> findByEmail(@Param("email") String email);

    // Trova utente per username O email (per login)
    @Query("SELECT u FROM User u WHERE (u.username = :usernameOrEmail OR u.email = :usernameOrEmail) AND u.deletedAt IS NULL")
    Optional<User> findByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);

    // Verifica se username esiste (anche tra soft deleted)
    boolean existsByUsername(String username);

    // Verifica se email esiste (anche tra soft deleted)
    boolean existsByEmail(String email);

    // Trova tutti gli utenti attivi (no soft deleted)
    @Query("SELECT u FROM User u WHERE u.deletedAt IS NULL")
    List<User> findAllActiveUsers();

    // Trova tutti gli utenti soft deleted
    @Query("SELECT u FROM User u WHERE u.deletedAt IS NOT NULL")
    List<User> findAllDeletedUsers();

    // Trova utenti per ruolo (solo attivi)
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.deletedAt IS NULL")
    List<User> findByRoleName(@Param("roleName") String roleName);

    // Trova utenti con email non verificata (solo attivi)
    @Query("SELECT u FROM User u WHERE u.emailVerified = false AND u.deletedAt IS NULL")
    List<User> findUsersWithUnverifiedEmail();

    // Trova utenti disabilitati (solo attivi - no soft deleted)
    @Query("SELECT u FROM User u WHERE u.enabled = false AND u.deletedAt IS NULL")
    List<User> findDisabledUsers();

    // Override del findById per escludere soft deleted
    @Query("SELECT u FROM User u WHERE u.id = :id AND u.deletedAt IS NULL")
    Optional<User> findActiveById(@Param("id") Long id);

    // Cerca utenti per nome/cognome (like - case insensitive)
    @Query("SELECT u FROM User u WHERE (LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%'))) AND u.deletedAt IS NULL")
    List<User> searchByName(@Param("search") String search);
}
