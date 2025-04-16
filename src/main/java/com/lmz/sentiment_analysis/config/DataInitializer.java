package com.lmz.sentiment_analysis.config;

import com.lmz.sentiment_analysis.model.Role;
import com.lmz.sentiment_analysis.model.User;
import com.lmz.sentiment_analysis.repository.RoleRepository;
import com.lmz.sentiment_analysis.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
//This class is a Spring Boot component that implements CommandLineRunner.
//It initializes the necessary data such as ensuring that required roles (e.g., "ROLE_ADMIN" and "ROLE_USER") exist
//It ensures that an admin user is created with a default password if it does not already exist.
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    //This method runs at application startup to initialize essential data.
    //It ensures that necessary roles exist and creates a default admin user if it is not already present.
    public void run(String... args) {

        //Check if the Admin role exists, if not, create and save it
        Role adminRole = roleRepository.findByName("ROLE_ADMIN");
        if (adminRole == null) {
            adminRole = new Role("ROLE_ADMIN");
            roleRepository.save(adminRole);
        }

        // Check if the USER role exists; if not, create and save it.
        Role userRole = roleRepository.findByName("ROLE_USER");
        if (userRole == null) {
            userRole = new Role("ROLE_USER");
            roleRepository.save(userRole);
        }

        // Check if the admin user exists; if not, create a default admin user.
        Optional<User> adminUserOpt = userRepository.findByUsername("admin");
        if (adminUserOpt.isEmpty()) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            // Encode the default password before saving for security reasons.
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            // Assign the ADMIN role to the newly created admin user.
            adminUser.addRole(adminRole);
            userRepository.save(adminUser);
        }
    }
}