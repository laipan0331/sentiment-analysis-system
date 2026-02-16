package com.lmz.sentiment_analysis.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Security Analysis Service for detecting malicious content and analyzing
 * security-related sentiment in text. This adds a cybersecurity dimension
 * to the sentiment analysis system.
 */
@Service
public class SecurityAnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(SecurityAnalysisService.class);

    // Malicious patterns - common indicators of threats
    private static final List<Pattern> MALICIOUS_PATTERNS = Arrays.asList(
        Pattern.compile("\\b(exploit|vulnerability|malware|ransomware|phishing)\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\b(hack|breach|attack|threat|trojan)\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\b(injection|xss|csrf|backdoor|payload)\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\b(ddos|botnet|keylogger|spyware|rootkit)\\b", Pattern.CASE_INSENSITIVE)
    );

    // Critical security keywords
    private static final List<String> CRITICAL_KEYWORDS = Arrays.asList(
        "critical", "severe", "urgent", "immediate", "emergency",
        "zero-day", "patch", "update", "mitigation"
    );

    // Risk severity levels
    public enum RiskLevel {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    /**
     * Analyze text for security-related content and risk level.
     * 
     * @param text The text to analyze
     * @return Security analysis result
     */
    public SecurityAnalysisResult analyzeSecurityContent(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new SecurityAnalysisResult(false, RiskLevel.LOW, 0, new ArrayList<>());
        }

        boolean isThreat = false;
        int threatScore = 0;
        List<String> detectedThreats = new ArrayList<>();

        // Check for malicious patterns
        for (Pattern pattern : MALICIOUS_PATTERNS) {
            if (pattern.matcher(text).find()) {
                isThreat = true;
                threatScore += 20;
                detectedThreats.add(pattern.pattern());
            }
        }

        // Check for critical keywords
        String lowerText = text.toLowerCase();
        for (String keyword : CRITICAL_KEYWORDS) {
            if (lowerText.contains(keyword)) {
                threatScore += 10;
            }
        }

        // Calculate risk level
        RiskLevel riskLevel = calculateRiskLevel(threatScore);

        logger.info("Security analysis: isThreat={}, riskLevel={}, score={}", 
                    isThreat, riskLevel, threatScore);

        return new SecurityAnalysisResult(isThreat, riskLevel, threatScore, detectedThreats);
    }

    /**
     * Analyze threat intelligence report sentiment and severity.
     * 
     * @param report The threat intelligence report
     * @param sentiment The sentiment from NLP analysis
     * @return Threat analysis result
     */
    public ThreatAnalysisResult analyzeThreatReport(String report, String sentiment) {
        SecurityAnalysisResult securityResult = analyzeSecurityContent(report);
        
        // Combine sentiment and security analysis for threat assessment
        int severityScore = calculateSeverityScore(sentiment, securityResult);
        String recommendation = generateRecommendation(securityResult.getRiskLevel());

        return new ThreatAnalysisResult(
            sentiment,
            securityResult.getRiskLevel(),
            severityScore,
            securityResult.isThreat(),
            securityResult.getDetectedThreats(),
            recommendation
        );
    }

    /**
     * Calculate risk level based on threat score.
     */
    private RiskLevel calculateRiskLevel(int threatScore) {
        if (threatScore >= 60) return RiskLevel.CRITICAL;
        if (threatScore >= 40) return RiskLevel.HIGH;
        if (threatScore >= 20) return RiskLevel.MEDIUM;
        return RiskLevel.LOW;
    }

    /**
     * Calculate overall severity score combining sentiment and security analysis.
     */
    private int calculateSeverityScore(String sentiment, SecurityAnalysisResult securityResult) {
        int score = securityResult.getThreatScore();
        
        // Adjust based on sentiment
        if (sentiment.toLowerCase().contains("negative")) {
            score += 15;
        } else if (sentiment.toLowerCase().contains("very negative")) {
            score += 25;
        }
        
        return Math.min(score, 100);
    }

    /**
     * Generate recommendation based on risk level.
     */
    private String generateRecommendation(RiskLevel riskLevel) {
        switch (riskLevel) {
            case CRITICAL:
                return "IMMEDIATE ACTION REQUIRED: Critical security threat detected. " +
                       "Escalate to security team immediately and implement emergency protocols.";
            case HIGH:
                return "HIGH PRIORITY: Significant security concern identified. " +
                       "Review and address within 24 hours.";
            case MEDIUM:
                return "MODERATE PRIORITY: Potential security issue detected. " +
                       "Schedule review and assessment within 72 hours.";
            case LOW:
            default:
                return "LOW PRIORITY: Monitor and track. No immediate action required.";
        }
    }

    /**
     * Security analysis result container.
     */
    public static class SecurityAnalysisResult {
        private final boolean isThreat;
        private final RiskLevel riskLevel;
        private final int threatScore;
        private final List<String> detectedThreats;

        public SecurityAnalysisResult(boolean isThreat, RiskLevel riskLevel, 
                                     int threatScore, List<String> detectedThreats) {
            this.isThreat = isThreat;
            this.riskLevel = riskLevel;
            this.threatScore = threatScore;
            this.detectedThreats = detectedThreats;
        }

        public boolean isThreat() {
            return isThreat;
        }

        public RiskLevel getRiskLevel() {
            return riskLevel;
        }

        public int getThreatScore() {
            return threatScore;
        }

        public List<String> getDetectedThreats() {
            return detectedThreats;
        }
    }

    /**
     * Threat analysis result container for comprehensive reports.
     */
    public static class ThreatAnalysisResult {
        private final String sentiment;
        private final RiskLevel riskLevel;
        private final int severityScore;
        private final boolean isThreat;
        private final List<String> detectedThreats;
        private final String recommendation;

        public ThreatAnalysisResult(String sentiment, RiskLevel riskLevel, 
                                   int severityScore, boolean isThreat,
                                   List<String> detectedThreats, String recommendation) {
            this.sentiment = sentiment;
            this.riskLevel = riskLevel;
            this.severityScore = severityScore;
            this.isThreat = isThreat;
            this.detectedThreats = detectedThreats;
            this.recommendation = recommendation;
        }

        public String getSentiment() {
            return sentiment;
        }

        public RiskLevel getRiskLevel() {
            return riskLevel;
        }

        public int getSeverityScore() {
            return severityScore;
        }

        public boolean isThreat() {
            return isThreat;
        }

        public List<String> getDetectedThreats() {
            return detectedThreats;
        }

        public String getRecommendation() {
            return recommendation;
        }
    }
}
