package com.lmz.sentiment_analysis.service;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

/**
 * Hybrid NLP Processor combining Stanford CoreNLP with LLM verification.
 * Uses CoreNLP as the primary analysis engine and falls back to LLM
 * when confidence is low, creating a robust sentiment analysis pipeline.
 */
@Service
@Primary
public class HybridNLPProcessor {

    private static final Logger logger = LoggerFactory.getLogger(HybridNLPProcessor.class);

    private final StanfordCoreNLP pipeline;
    private final LLMVerificationService llmVerificationService;

    @Value("${openai.confidence.threshold:0.7}")
    private double confidenceThreshold;

    @Autowired
    public HybridNLPProcessor(LLMVerificationService llmVerificationService) {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,parse,sentiment");
        this.pipeline = new StanfordCoreNLP(props);
        this.llmVerificationService = llmVerificationService;
    }

    /**
     * Analyze sentiment using hybrid approach:
     * 1. First use CoreNLP for fast analysis
     * 2. If confidence is low, verify with LLM
     * 
     * @param text The text to analyze
     * @return Sentiment analysis result with confidence
     */
    public SentimentResult analyzeSentiment(String text) {
        // Phase 1: CoreNLP Analysis
        CoreNLPResult coreNLPResult = analyzeSentimentWithCoreNLP(text);
        
        logger.info("CoreNLP analysis: sentiment={}, confidence={}", 
                    coreNLPResult.sentiment, coreNLPResult.confidence);

        // If CoreNLP is confident, use its result
        if (coreNLPResult.confidence >= confidenceThreshold) {
            return new SentimentResult(
                coreNLPResult.sentiment, 
                coreNLPResult.confidence, 
                "CoreNLP"
            );
        }

        // Phase 2: LLM Verification for low-confidence cases
        logger.info("Low confidence detected. Verifying with LLM...");
        LLMVerificationService.LLMSentimentResult llmResult = 
            llmVerificationService.verifySentiment(text);

        if (llmResult.isSuccess()) {
            logger.info("LLM verification: sentiment={}, confidence={}", 
                       llmResult.getSentiment(), llmResult.getConfidence());
            return new SentimentResult(
                llmResult.getSentiment(), 
                llmResult.getConfidence(), 
                "Hybrid (CoreNLP + LLM)"
            );
        }

        // Fallback to CoreNLP if LLM fails
        logger.warn("LLM verification failed. Using CoreNLP result.");
        return new SentimentResult(
            coreNLPResult.sentiment, 
            coreNLPResult.confidence, 
            "CoreNLP (LLM fallback)"
        );
    }

    /**
     * Analyze sentiment using Stanford CoreNLP.
     */
    private CoreNLPResult analyzeSentimentWithCoreNLP(String text) {
        Annotation annotation = new Annotation(text);
        pipeline.annotate(annotation);

        for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
            String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
            
            // Calculate confidence based on sentiment distribution
            // CoreNLP provides sentiment scores from 0-4
            String sentimentValue = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class).toString();
            double confidence = calculateConfidence(sentiment, sentimentValue);
            
            return new CoreNLPResult(sentiment, confidence);
        }

        return new CoreNLPResult("Neutral", 0.5);
    }

    /**
     * Calculate confidence score based on sentiment strength.
     * This is a heuristic based on the extremity of the sentiment.
     */
    private double calculateConfidence(String sentiment, String sentimentTree) {
        switch (sentiment.toLowerCase()) {
            case "very positive":
            case "very negative":
                return 0.85; // High confidence for extreme sentiments
            case "positive":
            case "negative":
                return 0.70; // Medium confidence
            case "neutral":
                return 0.55; // Lower confidence for neutral
            default:
                return 0.50;
        }
    }

    /**
     * Inner class for CoreNLP results.
     */
    private static class CoreNLPResult {
        final String sentiment;
        final double confidence;

        CoreNLPResult(String sentiment, double confidence) {
            this.sentiment = sentiment;
            this.confidence = confidence;
        }
    }

    /**
     * Public result class for sentiment analysis.
     */
    public static class SentimentResult {
        private final String sentiment;
        private final double confidence;
        private final String source;

        public SentimentResult(String sentiment, double confidence, String source) {
            this.sentiment = sentiment;
            this.confidence = confidence;
            this.source = source;
        }

        public String getSentiment() {
            return sentiment;
        }

        public double getConfidence() {
            return confidence;
        }

        public String getSource() {
            return source;
        }
    }
}
