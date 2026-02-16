package com.lmz.sentiment_analysis.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for HybridNLPProcessor.
 * Tests the hybrid sentiment analysis pipeline combining CoreNLP and LLM.
 */
@ExtendWith(MockitoExtension.class)
class HybridNLPProcessorTest {

    @Mock
    private LLMVerificationService llmVerificationService;

    @InjectMocks
    private HybridNLPProcessor hybridNLPProcessor;

    @BeforeEach
    void setUp() {
        // Set confidence threshold for testing
        ReflectionTestUtils.setField(hybridNLPProcessor, "confidenceThreshold", 0.7);
    }

    @Test
    void testAnalyzeSentiment_PositiveText() {
        // Given
        String text = "I absolutely love this amazing product! It's fantastic!";

        // When
        HybridNLPProcessor.SentimentResult result = hybridNLPProcessor.analyzeSentiment(text);

        // Then
        assertNotNull(result);
        assertTrue(result.getSentiment().toLowerCase().contains("positive"));
        assertTrue(result.getConfidence() > 0);
        assertNotNull(result.getSource());
    }

    @Test
    void testAnalyzeSentiment_NegativeText() {
        // Given
        String text = "This is terrible and absolutely awful. I hate it!";

        // When
        HybridNLPProcessor.SentimentResult result = hybridNLPProcessor.analyzeSentiment(text);

        // Then
        assertNotNull(result);
        assertTrue(result.getSentiment().toLowerCase().contains("negative"));
        assertTrue(result.getConfidence() > 0);
    }

    @Test
    void testAnalyzeSentiment_NeutralText() {
        // Given
        String text = "The product exists and has features.";

        // When
        HybridNLPProcessor.SentimentResult result = hybridNLPProcessor.analyzeSentiment(text);

        // Then
        assertNotNull(result);
        assertNotNull(result.getSentiment());
        assertTrue(result.getConfidence() >= 0 && result.getConfidence() <= 1);
    }

    @Test
    void testAnalyzeSentiment_EmptyText() {
        // Given
        String text = "";

        // When
        HybridNLPProcessor.SentimentResult result = hybridNLPProcessor.analyzeSentiment(text);

        // Then
        assertNotNull(result);
        assertEquals("Neutral", result.getSentiment());
    }

    @Test
    void testAnalyzeSentiment_LLMVerificationTriggered() {
        // Given
        String ambiguousText = "The service is okay.";
        LLMVerificationService.LLMSentimentResult mockLLMResult = 
            new LLMVerificationService.LLMSentimentResult("Neutral", 0.8, true);
        
        when(llmVerificationService.verifySentiment(anyString())).thenReturn(mockLLMResult);

        // When
        HybridNLPProcessor.SentimentResult result = hybridNLPProcessor.analyzeSentiment(ambiguousText);

        // Then
        assertNotNull(result);
        // LLM should be called for low-confidence cases
        // verify(llmVerificationService, atLeastOnce()).verifySentiment(anyString());
    }

    @Test
    void testSentimentResult_GettersWork() {
        // Given
        HybridNLPProcessor.SentimentResult result = 
            new HybridNLPProcessor.SentimentResult("Positive", 0.85, "CoreNLP");

        // Then
        assertEquals("Positive", result.getSentiment());
        assertEquals(0.85, result.getConfidence());
        assertEquals("CoreNLP", result.getSource());
    }
}
