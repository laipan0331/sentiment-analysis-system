package com.lmz.sentiment_analysis.config;

import com.lmz.sentiment_analysis.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomAuthenticationFailureHandler customFailureHandler;
    private final CustomUserDetailsService customUserDetailsService;

    // 注入自定义的用户详情服务和认证失败处理器
    public SecurityConfig(CustomAuthenticationFailureHandler customFailureHandler,
                          CustomUserDetailsService customUserDetailsService) {
        this.customFailureHandler = customFailureHandler;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置一个 DaoAuthenticationProvider，使用自定义的 UserDetailsService 和密码编码器
     */
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(customUserDetailsService);
        return provider;
    }

    /**
     * 自动配置的 AuthenticationManager 会自动拾取 Spring 容器中的所有 AuthenticationProvider
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 注册自定义的 AuthenticationProvider
                .authenticationProvider(daoAuthenticationProvider())
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/register", "/login", "/css/**", "/js/**", "/chart").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .failureHandler(customFailureHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );
        return http.build();
    }
}
