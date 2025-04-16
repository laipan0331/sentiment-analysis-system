package com.lmz.sentiment_analysis.repository;

import com.lmz.sentiment_analysis.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
//This repository interface manages Role entities.
// It extends JpaRepository to provide CRUD operations and includes a custom query method to find a role by its unique name.
public interface RoleRepository extends JpaRepository<Role, Long> {
    // Custom query method to find a Role entity based on its name.
    Role findByName(String name);
}