package com.lmz.sentiment_analysis.service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * LLM-based sentiment verification service using OpenAI API.
 * This service provides a secondary validation layer for sentiment analysis
 * when CoreNLP confidence is low.
 */
@Service
public class LLMVerificationService {

    private static final Logger logger = LoggerFactory.getLogger(LLMVerificationService.class);

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.model:gpt-3.5-turbo}")
    private String model;

    private OpenAiService openAiService;

    /**
     * Verify sentiment using LLM when CoreNLP confidence is low.
     * 
     * @param text The text to analyze
     * @return Sentiment analysis result
     */
    public LLMSentimentResult verifySentiment(String text) {
        try {
            if (openAiService == null && !apiKey.equals("test-key") && !apiKey.equals("your-api-key-here")) {
                openAiService = new OpenAiService(apiKey, Duration.ofSeconds(30));
            }

            if (openAiService == null) {
                logger.warn("OpenAI service not initialized. Using fallback.");
                return new LLMSentimentResult("Neutral", 0.5, false);
            }

            List<ChatMessage> messages = new ArrayList<>();
            messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), 
                "You are a sentiment analysis expert. Analyze the sentiment of the given text and respond ONLY with one of these exact labels: 'Very Positive', 'Positive', 'Neutral', 'Negative', or 'Very Negative'. Also provide a confidence score between 0 and 1 on a new line. Format: SENTIMENT\\nCONFIDENCE"));
            messages.add(new ChatMessage(ChatMessageRole.USER.value(), 
                "Analyze the sentiment of this text: " + text));

            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model(model)
                    .messages(messages)
                    .temperature(0.3)
                    .maxTokens(50)
                    .build();

            String response = openAiService.createChatCompletion(request)
                    .getChoices().get(0).getMessage().getContent().trim();

            return parseResponse(response);

        } catch (Exception e) {
            logger.error("Error calling OpenAI API: {}", e.getMessage());
            return new LLMSentimentResult("Neutral", 0.5, false);
        }
    }

    /**
     * Parse LLM response to extract sentiment and confidence.
     */
    private LLMSentimentResult parseResponse(String response) {
        try {
            String[] parts = response.split("\\n");
            String sentiment = parts[0].trim();
            double confidence = parts.length > 1 ? Double.parseDouble(parts[1].trim()) : 0.7;
            
            // Normalize sentiment labels
            sentiment = normalizeSentiment(sentiment);
            
            return new LLMSentimentResult(sentiment, confidence, true);
        } catch (Exception e) {
            logger.error("Error parsing LLM response: {}", e.getMessage());
            return new LLMSentimentResult("Neutral", 0.5, false);
        }
    }

    /**
     * Normalize sentiment labels to match expected format.
     */
    private String normalizeSentiment(String sentiment) {
        String lower = sentiment.toLowerCase();
        if (lower.contains("very positive")) return "Very positive";
        if (lower.contains("very negative")) return "Very negative";
        if (lower.contains("positive")) return "Positive";
        if (lower.contains("negative")) return "Negative";
        return "Neutral";
    }

    /**
     * Result container for LLM sentiment analysis.
     */
    public static class LLMSentimentResult {
        private final String sentiment;
        private final double confidence;
        private final boolean success;

        public LLMSentimentResult(String sentiment, double confidence, boolean success) {
            this.sentiment = sentiment;
            this.confidence = confidence;
            this.success = success;
        }

        public String getSentiment() {
            return sentiment;
        }

        public double getConfidence() {
            return confidence;
        }

        public boolean isSuccess() {
            return success;
        }
    }
}
