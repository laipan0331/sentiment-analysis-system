package com.lmz.sentiment_analysis.repository;

import com.lmz.sentiment_analysis.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    // 根据用户名查找用户，返回 Optional<User>
    Optional<User> findByUsername(String username);
}
