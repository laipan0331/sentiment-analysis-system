package com.lmz.sentiment_analysis.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.lmz.sentiment_analysis.model.Comment;
//This repository interface provides CRUD operations for Comment entities.
// It extends JpaRepository to inherit several methods for working with Comment persistence,
// it includes a custom method to find comments by a given user's ID.
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // Custom query method to find a list of comments by the specified user ID.
    List<Comment> findByUserId(Long userId);
}
