package com.lmz.sentiment_analysis.service;

import com.lmz.sentiment_analysis.model.Role;
import com.lmz.sentiment_analysis.model.User;
import com.lmz.sentiment_analysis.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
//This service implements Spring Security's UserDetailsService to load user-specific data during authentication.
//It retrieves the User entity from the database by username, converts the user's roles into granted authorities。
//It returns a UserDetails object that Spring Security uses for authentication and authorization.
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    //Constructor for UserDetailsServiceImpl with dependency injection.
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    //Loads the user by their username.
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Retrieve the User entity from the repository; throw exception if not found.
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Convert the user's roles into a list of granted authorities.
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        if (user.getRoles() != null) {
            //Map each role to a SimpleGrantedAuthority object.
            authorities = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName()))
                    .collect(Collectors.toList());
        }

        // If the user has no roles assigned, grant a default role of ROLE_USER.
        if (authorities.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        // Create and return a UserDetails object using Spring Security's built-in implementation.
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}