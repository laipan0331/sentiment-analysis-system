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
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/add")
    public ResponseEntity<Comment> addComment(@RequestParam String content) {
        Comment savedComment = commentService.addComment(content);
        return ResponseEntity.ok(savedComment);
    }

    // 当前用户查看自己的评论
    @GetMapping("/my")
    public ResponseEntity<List<Comment>> getMyComments() {
        List<Comment> myComments = commentService.getAllComments();
        return ResponseEntity.ok(myComments);
    }

    // 新增接口：查看全局评论情感分布数据
    @GetMapping("/sentiment-distribution")
    public ResponseEntity<Map<String, Long>> getGlobalSentimentDistribution() {
        Map<String, Long> distribution = commentService.getGlobalSentimentDistribution();
        return ResponseEntity.ok(distribution);
    }
}
