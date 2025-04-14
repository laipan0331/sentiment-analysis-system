package com.lmz.sentiment_analysis.service;

import com.lmz.sentiment_analysis.model.User;
import com.lmz.sentiment_analysis.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerUser(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("username already exists");
        }
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(username, encodedPassword, "ROLE_USER");
        return userRepository.save(user);
    }
}
