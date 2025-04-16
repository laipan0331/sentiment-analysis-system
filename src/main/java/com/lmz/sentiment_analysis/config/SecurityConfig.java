package com.lmz.sentiment_analysis.config;

import com.lmz.sentiment_analysis.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
//This configuration class sets up Spring Security for the application.
// It defines the security filter chain, authentication manager, success and failure handlers for login events, and a password encoder.
// Specific URL patterns are protected based on roles and request matchers, allowing for custom authentication and authorization rules.
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/register", "/css/**", "/js/**", "/h2-console/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/login")
                        .defaultSuccessUrl("/")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .csrf(csrf -> csrf
                        //Disables CSRF protection for H2 console URLs
                        .ignoringRequestMatchers("/h2-console/**")
                )
                .headers(headers -> headers
                        //Allows H2 console to be displayed in a frame from the same origin
                        .frameOptions(options -> options.sameOrigin())
                )
                .userDetailsService(userDetailsService);

        return http.build();
    }

    @Bean
    //Defines the PasswordEncoder bean using BCrypt for secure password hashing
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
