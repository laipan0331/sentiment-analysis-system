package com.lmz.sentiment_analysis.service;

import com.lmz.sentiment_analysis.model.Comment;
import com.lmz.sentiment_analysis.model.User;
import com.lmz.sentiment_analysis.repository.CommentRepository;
import com.lmz.sentiment_analysis.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CommentServiceImpl.
 * Tests comment operations, sentiment analysis integration, and caching behavior.
 */
@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private HybridNLPProcessor hybridNLPProcessor;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CommentServiceImpl commentService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testAddComment_PositiveSentiment() {
        // Given
        String content = "This is a great product!";
        HybridNLPProcessor.SentimentResult sentimentResult = 
            new HybridNLPProcessor.SentimentResult("Positive", 0.85, "CoreNLP");
        
        when(hybridNLPProcessor.analyzeSentiment(anyString())).thenReturn(sentimentResult);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("testuser");
        when(authentication.getName()).thenReturn("testuser");
        
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        
        Comment savedComment = new Comment(content);
        savedComment.setSentiment("Positive");
        savedComment.setUserId(1L);
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        // When
        Comment result = commentService.addComment(content);

        // Then
        assertNotNull(result);
        assertEquals("Positive", result.getSentiment());
        verify(hybridNLPProcessor, times(1)).analyzeSentiment(content);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void testAddComment_NegativeSentiment() {
        // Given
        String content = "This is terrible!";
        HybridNLPProcessor.SentimentResult sentimentResult = 
            new HybridNLPProcessor.SentimentResult("Negative", 0.80, "CoreNLP");
        
        when(hybridNLPProcessor.analyzeSentiment(anyString())).thenReturn(sentimentResult);
        when(securityContext.getAuthentication()).thenReturn(null);
        
        Comment savedComment = new Comment(content);
        savedComment.setSentiment("Negative");
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        // When
        Comment result = commentService.addComment(content);

        // Then
        assertNotNull(result);
        assertEquals("Negative", result.getSentiment());
        assertEquals("😞", result.getEmoji());
        assertEquals("#808080", result.getBackgroundColor());
    }

    @Test
    void testAddComment_VeryPositiveSentiment() {
        // Given
        String content = "Absolutely amazing and wonderful!";
        HybridNLPProcessor.SentimentResult sentimentResult = 
            new HybridNLPProcessor.SentimentResult("Very positive", 0.95, "CoreNLP");
        
        when(hybridNLPProcessor.analyzeSentiment(anyString())).thenReturn(sentimentResult);
        when(securityContext.getAuthentication()).thenReturn(null);
        
        Comment savedComment = new Comment(content);
        savedComment.setSentiment("Very positive");
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        // When
        Comment result = commentService.addComment(content);

        // Then
        assertNotNull(result);
        assertEquals("Very positive", result.getSentiment());
        assertEquals("😄", result.getEmoji());
        assertEquals("#ffff00", result.getBackgroundColor());
        assertEquals(0.95, result.getSentimentScore());
    }

    @Test
    void testGetAllComments_WithAuthenticatedUser() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("testuser");
        when(authentication.getName()).thenReturn("testuser");
        
        User user = new User();
        user.setId(1L);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        
        List<Comment> userComments = Arrays.asList(
            new Comment("Comment 1"),
            new Comment("Comment 2")
        );
        when(commentRepository.findByUserId(1L)).thenReturn(userComments);

        // When
        List<Comment> result = commentService.getAllComments();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(commentRepository, times(1)).findByUserId(1L);
    }

    @Test
    void testGetAllComments_WithoutAuthentication() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(null);
        
        List<Comment> allComments = Arrays.asList(
            new Comment("Comment 1"),
            new Comment("Comment 2"),
            new Comment("Comment 3")
        );
        when(commentRepository.findAll()).thenReturn(allComments);

        // When
        List<Comment> result = commentService.getAllComments();

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(commentRepository, times(1)).findAll();
    }

    @Test
    void testGetGlobalSentimentDistribution() {
        // Given
        List<Comment> comments = Arrays.asList(
            createComment("Positive"),
            createComment("Positive"),
            createComment("Negative"),
            createComment("Neutral")
        );
        when(commentRepository.findAll()).thenReturn(comments);

        // When
        Map<String, Long> distribution = commentService.getGlobalSentimentDistribution();

        // Then
        assertNotNull(distribution);
        assertEquals(2L, distribution.get("Positive"));
        assertEquals(1L, distribution.get("Negative"));
        assertEquals(1L, distribution.get("Neutral"));
    }

    private Comment createComment(String sentiment) {
        Comment comment = new Comment("Test content");
        comment.setSentiment(sentiment);
        return comment;
    }
}
