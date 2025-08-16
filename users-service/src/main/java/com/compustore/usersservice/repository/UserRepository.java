package com.compustore.usersservice.repository;

import com.compustore.usersservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for managing {@link User} entities.  Spring Data JPA will
 * automatically implement common CRUD methods.  The findByUsername method
 * returns an Optional so the service can handle absent users gracefully.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}