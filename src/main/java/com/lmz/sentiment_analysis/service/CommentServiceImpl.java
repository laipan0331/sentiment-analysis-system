package com.lmz.sentiment_analysis.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lmz.sentiment_analysis.model.Comment;
import com.lmz.sentiment_analysis.repository.CommentRepository;
import com.lmz.sentiment_analysis.util.SecurityUtil;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final NLPProcessor nlpProcessor;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, NLPProcessor nlpProcessor) {
        this.commentRepository = commentRepository;
        this.nlpProcessor = nlpProcessor;
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
        // 对于用户发布的评论（仅记录当前登录用户的评论）：
        Long currentUserId = SecurityUtil.getCurrentUserId();
        comment.setUserId(currentUserId);
        return commentRepository.save(comment);
    }

    /**
     * 返回当前登录用户的评论（现有方法）
     */
    @Override
    public List<Comment> getAllComments() {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        return commentRepository.findByUserId(currentUserId);
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
}
