package com.ethnicdev.identity.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ethnicdev.identity.entity.User;

/**
 * User repository.
 *
 * @author Thuol-S
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * Check user exists by user nanme.
     *
     * @param username username of user
     * @return true: User eixsts, false: User not exists
     */
    boolean existsByUsername(String username);

    /**
     * Find user by username.
     *
     * @param username Username of user
     * @return Optional of user entity
     */
    Optional<User> findByUsername(String username);
}
