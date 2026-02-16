package com.lmz.sentiment_analysis.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LLMVerificationService.
 * Tests the LLM-based sentiment verification functionality.
 */
class LLMVerificationServiceTest {

    private LLMVerificationService llmVerificationService;

    @BeforeEach
    void setUp() {
        llmVerificationService = new LLMVerificationService();
        // Set test API key to prevent actual API calls
        ReflectionTestUtils.setField(llmVerificationService, "apiKey", "test-key");
        ReflectionTestUtils.setField(llmVerificationService, "model", "gpt-3.5-turbo");
    }

    @Test
    void testVerifySentiment_WithTestKey_ReturnsFallback() {
        // Given
        String text = "This is a test message.";

        // When
        LLMVerificationService.LLMSentimentResult result = 
            llmVerificationService.verifySentiment(text);

        // Then
        assertNotNull(result);
        assertNotNull(result.getSentiment());
        assertTrue(result.getConfidence() >= 0 && result.getConfidence() <= 1);
    }

    @Test
    void testLLMSentimentResult_Getters() {
        // Given
        LLMVerificationService.LLMSentimentResult result = 
            new LLMVerificationService.LLMSentimentResult("Positive", 0.85, true);

        // Then
        assertEquals("Positive", result.getSentiment());
        assertEquals(0.85, result.getConfidence());
        assertTrue(result.isSuccess());
    }

    @Test
    void testLLMSentimentResult_FailureCase() {
        // Given
        LLMVerificationService.LLMSentimentResult result = 
            new LLMVerificationService.LLMSentimentResult("Neutral", 0.5, false);

        // Then
        assertEquals("Neutral", result.getSentiment());
        assertEquals(0.5, result.getConfidence());
        assertFalse(result.isSuccess());
    }
}
