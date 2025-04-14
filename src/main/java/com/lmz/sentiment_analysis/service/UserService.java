package com.lmz.sentiment_analysis.service;

import com.lmz.sentiment_analysis.model.User;
import java.util.List;

public interface UserService {
    User findByUsername(String username);
    User registerNewUser(User user);
    List<User> findAllUsers();
    void deleteUser(Long id);

    boolean isAdmin(User user);
}