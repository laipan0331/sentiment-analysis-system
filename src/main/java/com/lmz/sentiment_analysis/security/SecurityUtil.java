package com.lmz.sentiment_analysis.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
//This utility class provides helper methods for accessing security-related information.
//It offers methods to retrieve the current user's username and to check whether the user has admin privileges by examining the security context.
public class SecurityUtil {
    //Retrieves the current authenticated username from the security context.
    public static String getCurrentUsername() {
        // Get the current Authentication object from the SecurityContext.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Check if there is no authentication or if the user is not authenticated.
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        // Retrieve the principal which holds the user details.
        Object principal = authentication.getPrincipal();
        // If principal is an instance of UserDetails, return the username.
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        // Otherwise, fallback to the principal's toString representation.
        return principal != null ? principal.toString() : null;
    }
    //Checks if the current authenticated user has the 'ROLE_ADMIN' authority.
    public static boolean isAdmin() {
        // Get the current Authentication object from the SecurityContext.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null &&
                // Stream through the granted authorities and check if any match 'ROLE_ADMIN'.
                authentication.getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}