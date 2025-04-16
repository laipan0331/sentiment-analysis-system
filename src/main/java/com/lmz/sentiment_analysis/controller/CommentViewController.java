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
//This Spring MVC controller handles form submissions for comments.
//It processes a submitted comment, retrieves the resulting sentiment analysis information (emoji, sentiment, score, and background color), and then passes these attributes to the view for display on the result page.
public class CommentViewController {

    @Autowired
    private CommentService commentService;

    //Handles POST requests to submit a comment.
    @PostMapping("/submit")
    public String submitComment(@RequestParam("content") String content, Model model) {
        // Process the submitted comment and obtain a Comment object with sentiment analysis results.
        Comment savedComment = commentService.addComment(content);
        // Add sentiment analysis results and original content into the model to be used in the view.
        model.addAttribute("emoji", savedComment.getEmoji());
        model.addAttribute("sentiment", savedComment.getSentiment());
        model.addAttribute("score", savedComment.getSentimentScore());
        model.addAttribute("bgColor", savedComment.getBackgroundColor());
        model.addAttribute("content", savedComment.getContent());
        // Return the view name "result" to display the analysis results.
        return "result";
    }
}

