package com.lmz.sentiment_analysis.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.lmz.sentiment_analysis.model.Comment;
import com.lmz.sentiment_analysis.model.User;
import com.lmz.sentiment_analysis.repository.CommentRepository;
import com.lmz.sentiment_analysis.repository.UserRepository;

@Service
//This service implements the CommentService interface and provides functionality to add comments, retrieve comments, and compute a global sentiment distribution.
// It uses an NLPProcessor to analyze the sentiment of the comment's content and sets various properties (emoji, background color, sentiment score) based on the analysis.
//Additionally, it assigns the comment to the currently authenticated user when available.
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final NLPProcessor nlpProcessor;
    private final UserRepository userRepository;

    @Autowired
    //Constructor with dependencies injected.
    public CommentServiceImpl(CommentRepository commentRepository,
                              NLPProcessor nlpProcessor,
                              UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.nlpProcessor = nlpProcessor;
        this.userRepository = userRepository;
    }

    @Override
    //Adds a new comment with the analyzed sentiment and corresponding properties.
    public Comment addComment(String content) {
        Comment comment = new Comment(content);
        String sentiment = nlpProcessor.analyzeSentiment(content);
        comment.setSentiment(sentiment);

        // Set emoji, background color, and sentiment score based on the sentiment result.
        switch (sentiment.toLowerCase()) {
            case "very positive":
                comment.setEmoji("😄");
                comment.setBackgroundColor("#ffff00");
                comment.setSentimentScore(0.95);
                break;
            case "positive":
                comment.setEmoji("😊");
                comment.setBackgroundColor("#D0F0C0");
                comment.setSentimentScore(0.85);
                break;
            case "negative":
                comment.setEmoji("😞");
                comment.setBackgroundColor("#808080");
                comment.setSentimentScore(0.30);
                break;
            case "very negative":
                comment.setEmoji("😠");
                comment.setBackgroundColor("#be2528");
                comment.setSentimentScore(0.10);
                break;
            default:
                comment.setEmoji("😐");
                comment.setBackgroundColor("#FFFFFF");
                comment.setSentimentScore(0.50);
                break;
        }
        // Retrieve the current user's ID and assign it to the comment.
        Long currentUserId = getCurrentUserId();
        if (currentUserId != null) {
            comment.setUserId(currentUserId);
        }
        // Save the comment using the repository and return the saved object.
        return commentRepository.save(comment);
    }

    @Override
    //Retrieves comments associated with the current user if authenticated, otherwise retrieves all comments.
    public List<Comment> getAllComments() {
        Long currentUserId = getCurrentUserId();
        if (currentUserId != null) {
            return commentRepository.findByUserId(currentUserId);
        } else {
            return commentRepository.findAll();
        }
    }

    @Override
    //Computes the global sentiment distribution across all comments.
    public Map<String, Long> getGlobalSentimentDistribution() {
        return commentRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        Comment::getSentiment,
                        Collectors.counting()
                ));
    }

    //Retrieves the current authenticated user's ID from the security context.
    private Long getCurrentUserId() {
        // Get authentication details from the security context.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }

        String username = authentication.getName();
        // Find the user by username and return their ID, or null if not found.
        return userRepository.findByUsername(username)
                .map(User::getId)
                .orElse(null);
    }
}