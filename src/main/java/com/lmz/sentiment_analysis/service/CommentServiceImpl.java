package com.lmz.sentiment_analysis.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.lmz.sentiment_analysis.model.Comment;
import com.lmz.sentiment_analysis.model.User;
import com.lmz.sentiment_analysis.repository.CommentRepository;
import com.lmz.sentiment_analysis.repository.UserRepository;

@Service
public class CommentServiceImpl implements CommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);

    private final CommentRepository commentRepository;
    private final HybridNLPProcessor hybridNLPProcessor;
    private final UserRepository userRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository,
                              HybridNLPProcessor hybridNLPProcessor,
                              UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.hybridNLPProcessor = hybridNLPProcessor;
        this.userRepository = userRepository;
    }

    @Override
    @CacheEvict(value = {"comments", "sentimentDistribution"}, allEntries = true)
    public Comment addComment(String content) {
        Comment comment = new Comment(content);
        
        // Use hybrid NLP processor for sentiment analysis
        HybridNLPProcessor.SentimentResult result = hybridNLPProcessor.analyzeSentiment(content);
        comment.setSentiment(result.getSentiment());

        logger.info("Sentiment analysis completed: sentiment={}, confidence={}, source={}", 
                    result.getSentiment(), result.getConfidence(), result.getSource());

        // Set emoji, background color, and sentiment score based on the sentiment result
        switch (result.getSentiment().toLowerCase()) {
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
        
        Long currentUserId = getCurrentUserId();
        if (currentUserId != null) {
            comment.setUserId(currentUserId);
        }
        
        return commentRepository.save(comment);
    }

    @Override
    @Cacheable(value = "comments", key = "#root.methodName + '_' + @commentServiceImpl.getCurrentUserId()")
    public List<Comment> getAllComments() {
        Long currentUserId = getCurrentUserId();
        if (currentUserId != null) {
            return commentRepository.findByUserId(currentUserId);
        } else {
            return commentRepository.findAll();
        }
    }

    @Override
    @Cacheable(value = "sentimentDistribution")
    public Map<String, Long> getGlobalSentimentDistribution() {
        return commentRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        Comment::getSentiment,
                        Collectors.counting()
                ));
    }

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }

        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .map(User::getId)
                .orElse(null);
    }
}