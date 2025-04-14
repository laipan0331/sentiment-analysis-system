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
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final NLPProcessor nlpProcessor;
    private final UserRepository userRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository,
                              NLPProcessor nlpProcessor,
                              UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.nlpProcessor = nlpProcessor;
        this.userRepository = userRepository;
    }

    @Override
    public Comment addComment(String content) {
        Comment comment = new Comment(content);
        String sentiment = nlpProcessor.analyzeSentiment(content);
        comment.setSentiment(sentiment);

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

        // 获取当前登录用户
        Long currentUserId = getCurrentUserId();
        if (currentUserId != null) {
            comment.setUserId(currentUserId);
        }

        return commentRepository.save(comment);
    }

    /**
     * 返回当前登录用户的评论
     */
    @Override
    public List<Comment> getAllComments() {
        Long currentUserId = getCurrentUserId();
        if (currentUserId != null) {
            return commentRepository.findByUserId(currentUserId);
        } else {
            // 如果用户未登录，返回空列表或所有评论
            return commentRepository.findAll();
        }
    }

    /**
     * 返回全局所有评论的情感分布数据（用于数据可视化，不过滤用户）
     */
    @Override
    public Map<String, Long> getGlobalSentimentDistribution() {
        return commentRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        Comment::getSentiment,
                        Collectors.counting()
                ));
    }

    /**
     * 获取当前登录用户ID的辅助方法
     */
    private Long getCurrentUserId() {
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