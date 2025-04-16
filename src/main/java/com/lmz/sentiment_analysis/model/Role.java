package com.lmz.sentiment_analysis.model;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
//This entity represents a user role in the application. It is mapped to the "roles" table in the database
//It also stores a unique role name (such as "ROLE_ADMIN" or "ROLE_USER").
public class Role {
    @Id
    // Generates the ID value automatically.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // Ensures that the 'name' column is unique in the database.
    @Column(unique = true)
    private String name;
    //Default constructor required by JPA.
    public Role() {
    }

    public Role(String name) {
        this.name = name;
    }

    // Getters and Setters methods
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}