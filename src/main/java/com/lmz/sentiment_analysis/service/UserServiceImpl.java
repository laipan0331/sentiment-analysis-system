package com.lmz.sentiment_analysis.service;

import com.lmz.sentiment_analysis.model.Role;
import com.lmz.sentiment_analysis.model.User;
import com.lmz.sentiment_analysis.repository.RoleRepository;
import com.lmz.sentiment_analysis.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.HashSet;

@Service
//This service implements the UserService interface to manage user-related operations.
//It provides methods to find a user by username, register new users (with password encryption and default role assignment)
//It retrieves all users, delete a user by ID, and check if a user has administrative privileges.
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    //Constructor for UserServiceImpl. Dependencies are injected via constructor injection.
    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    //Finds a user by their username.
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    //Registers a new user in the system:
    // 1.Checks if the username already exists.
    // 2.Encrypts the user's password.
    // 3.Assigns the default role (ROLE_USER) to the new user.
    // 4.Saves the user to the database.
    public User registerNewUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists: " + user.getUsername());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role userRole = roleRepository.findByName("ROLE_USER");
        if (userRole == null) {
            userRole = new Role("ROLE_USER");
            roleRepository.save(userRole);
        }
        user.addRole(userRole);

        return userRepository.save(user);
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && user.hasRole("ROLE_ADMIN");
    }
}