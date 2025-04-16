package com.lmz.sentiment_analysis.controller;
import com.lmz.sentiment_analysis.model.Comment;
import com.lmz.sentiment_analysis.service.CommentService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
//This REST controller handles HTTP API requests related to comments.
// It provides endpoints for adding a comment, retrieving the current user's comments, and obtaining a global sentiment distribution of all comments.
public class CommentController {
    @Autowired
    private CommentService commentService;

    //Adds a new comment
    @PostMapping("/add")
    public ResponseEntity<Comment> addComment(@RequestParam String content) {
        Comment savedComment = commentService.addComment(content);
        return ResponseEntity.ok(savedComment);
    }

    //Retrieves a list of comments for the current user
    @GetMapping("/my")
    public ResponseEntity<List<Comment>> getMyComments() {
        List<Comment> myComments = commentService.getAllComments();
        return ResponseEntity.ok(myComments);
    }

    //Retrieves the global sentiment distribution for all comments
    @GetMapping("/sentiment-distribution")
    public ResponseEntity<Map<String, Long>> getGlobalSentimentDistribution() {
        Map<String, Long> distribution = commentService.getGlobalSentimentDistribution();
        return ResponseEntity.ok(distribution);
    }
}
