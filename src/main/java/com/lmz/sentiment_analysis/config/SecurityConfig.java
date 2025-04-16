package com.lmz.sentiment_analysis.config;

import com.lmz.sentiment_analysis.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import java.io.IOException;

@Configuration
@EnableWebSecurity
//This configuration class sets up Spring Security for the application.
// It defines the security filter chain, authentication manager, success and failure handlers for login events, and a password encoder.
// Specific URL patterns are protected based on roles and request matchers, allowing for custom authentication and authorization rules.
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    // Constructor injection for the custom UserDetailsService.
    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                //Specifies which UserDetailsService to use for authentication
                .userDetailsService(userDetailsService)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/register", "/css/**", "/js/**", "/h2-console/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/login")
                        .defaultSuccessUrl("/")
                        .successHandler(successHandler())
                        .failureHandler(failureHandler())
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll()
                )
                .csrf(csrf -> csrf
                        //Disables CSRF protection for H2 console URLs
                        .ignoringRequestMatchers("/h2-console/**")
                )
                .headers(headers -> headers
                        //Allows H2 console to be displayed in a frame from the same origin
                        .frameOptions(options -> options.sameOrigin())
                );

        return http.build();
    }

    //Defines the AuthenticationManager bean which integrates with the custom UserDetailsService and uses a BCrypt password encoder.
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        // Configure the authentication manager with the custom UserDetailsService and password encoder.
        authBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        return authBuilder.build();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(jakarta.servlet.http.HttpServletRequest request,
                                                jakarta.servlet.http.HttpServletResponse response,
                                                Authentication authentication)
                    throws IOException, ServletException {
                System.out.println("Login success: " + authentication.getName());
                System.out.println("Authorities: " + authentication.getAuthorities());
                response.sendRedirect("/");
            }
        };
    }

    @Bean
    //Custom handler for failed authentication events
    public AuthenticationFailureHandler failureHandler() {
        return new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(jakarta.servlet.http.HttpServletRequest request,
                                                jakarta.servlet.http.HttpServletResponse response,
                                                AuthenticationException exception)
                    throws IOException, ServletException {
                System.out.println("Login failed: " + exception.getMessage());
                System.out.println("Username attempted: " + request.getParameter("username"));
                response.sendRedirect("/login?error");
            }
        };
    }

    //Defines the PasswordEncoder bean using BCrypt for secure password hashing
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}