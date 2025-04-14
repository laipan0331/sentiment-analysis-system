package com.lmz.sentiment_analysis.security;

import com.lmz.sentiment_analysis.model.User; // 确保这是正确的User类路径
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
public class CustomUserDetailsServiceOld implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsServiceOld(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 查找用户，若不存在则抛异常
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // 创建权限列表
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        // 添加默认的USER角色
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        // 如果用户名是admin，添加ADMIN角色
        if ("admin".equals(username)) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        // 使用Spring Security的User类
        return new org.springframework.security.core.userdetails.User(
                username, // 直接使用参数中的username
                user.getPassword(), // 从User对象获取密码
                authorities
        );
    }
}