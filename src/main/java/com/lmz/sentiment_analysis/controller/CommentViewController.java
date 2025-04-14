package com.lmz.sentiment_analysis.controller;

import com.lmz.sentiment_analysis.model.Comment;
import com.lmz.sentiment_analysis.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/comments")
public class CommentViewController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/submit")
    public String submitComment(@RequestParam("content") String content, Model model) {

        Comment savedComment = commentService.addComment(content);

        model.addAttribute("emoji", savedComment.getEmoji());
        model.addAttribute("sentiment", savedComment.getSentiment());
        model.addAttribute("score", savedComment.getSentimentScore());
        model.addAttribute("bgColor", savedComment.getBackgroundColor());

        model.addAttribute("content", savedComment.getContent());

        return "result";
    }
}

