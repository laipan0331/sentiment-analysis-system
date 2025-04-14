package com.lmz.sentiment_analysis.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.lmz.sentiment_analysis.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByUserId(Long userId);
}
