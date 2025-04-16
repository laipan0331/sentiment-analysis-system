package com.lmz.sentiment_analysis.security;

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

@Service
//This service implements Spring Security's UserDetailsService to load user-specific data during authentication.
//It retrieves the user from the database by username and assigns a set of granted authorities (roles).
//If the username is "admin", the user is granted an additional admin role.
public class CustomUserDetailsServiceOld implements UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    // Constructor injection for UserRepository dependency.
    public CustomUserDetailsServiceOld(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    //Loads the user by the given username.
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Retrieve the User entity from the database; throw an exception if not found.
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        // Initialize a list to hold the granted authorities (roles) for the user.
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        // Every user is granted the default role ROLE_USER.
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        // If the username is "admin", grant additional admin privileges.
        if ("admin".equals(username)) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        // Return an instance of UserDetails using Spring Security's built-in User class.
        return new org.springframework.security.core.userdetails.User(
                username,
                user.getPassword(),
                authorities
        );
    }
}