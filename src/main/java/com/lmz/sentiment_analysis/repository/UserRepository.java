package com.lmz.sentiment_analysis.repository;

import com.lmz.sentiment_analysis.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

//This repository interface manages User entities.
// It extends JpaRepository to provide built-in CRUD operations for the User entity,
// It declares a custom method to find a user by their username.
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
}
