package com.retailsports.user_service.repository;

import com.retailsports.user_service.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    // Trova ruolo per nome
    Optional<Role> findByName(String name);

    // Verifica se ruolo esiste
    boolean existsByName(String name);
}
