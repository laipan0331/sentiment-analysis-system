package com.lmz.sentiment_analysis.service;

import com.lmz.sentiment_analysis.model.User;
import java.util.List;

//This interface defines the contract for user-related operations in the application.
//It provides methods to find a user by username, register a new user.
//It retrieves all users, delete a user by their ID, and check if a user is an administrator.
public interface UserService {
    User findByUsername(String username);
    User registerNewUser(User user);
    List<User> findAllUsers();
    void deleteUser(Long id);

    boolean isAdmin(User user);
}