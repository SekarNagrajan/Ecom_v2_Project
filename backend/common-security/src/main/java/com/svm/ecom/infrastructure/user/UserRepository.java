package com.svm.ecom.infrastructure.user;

import com.svm.ecom.domain.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing AppUser entities.
 * Extends JpaRepository to provide standard CRUD operations.
 */
@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {

    /**
     * Find a user by username.
     * This method is used for authentication purposes.
     *
     * @param username the username to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<AppUser> findByUsername(String username);

    /**
     * Check if a user exists with the given username.
     * Useful for validation during user registration.
     *
     * @param username the username to check
     * @return true if user exists, false otherwise
     */
    boolean existsByUsername(String username);
}