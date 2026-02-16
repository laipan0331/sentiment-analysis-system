package com.lmz.sentiment_analysis.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SecurityAnalysisService.
 * Tests security threat detection and risk analysis functionality.
 */
class SecurityAnalysisServiceTest {

    private SecurityAnalysisService securityAnalysisService;

    @BeforeEach
    void setUp() {
        securityAnalysisService = new SecurityAnalysisService();
    }

    @Test
    void testAnalyzeSecurityContent_MaliciousText() {
        // Given
        String text = "Critical vulnerability detected. Immediate exploit available for ransomware attack.";

        // When
        SecurityAnalysisService.SecurityAnalysisResult result = 
            securityAnalysisService.analyzeSecurityContent(text);

        // Then
        assertTrue(result.isThreat());
        assertNotEquals(SecurityAnalysisService.RiskLevel.LOW, result.getRiskLevel());
        assertTrue(result.getThreatScore() > 0);
        assertFalse(result.getDetectedThreats().isEmpty());
    }

    @Test
    void testAnalyzeSecurityContent_BenignText() {
        // Given
        String text = "The weather is nice today and I love sunshine.";

        // When
        SecurityAnalysisService.SecurityAnalysisResult result = 
            securityAnalysisService.analyzeSecurityContent(text);

        // Then
        assertFalse(result.isThreat());
        assertEquals(SecurityAnalysisService.RiskLevel.LOW, result.getRiskLevel());
        assertEquals(0, result.getThreatScore());
        assertTrue(result.getDetectedThreats().isEmpty());
    }

    @Test
    void testAnalyzeSecurityContent_EmptyText() {
        // Given
        String text = "";

        // When
        SecurityAnalysisService.SecurityAnalysisResult result = 
            securityAnalysisService.analyzeSecurityContent(text);

        // Then
        assertFalse(result.isThreat());
        assertEquals(SecurityAnalysisService.RiskLevel.LOW, result.getRiskLevel());
    }

    @Test
    void testAnalyzeSecurityContent_CriticalThreat() {
        // Given
        String text = "Zero-day exploit discovered. Critical malware attack with immediate threat. " +
                     "Urgent patch required. Ransomware vulnerability detected.";

        // When
        SecurityAnalysisService.SecurityAnalysisResult result = 
            securityAnalysisService.analyzeSecurityContent(text);

        // Then
        assertTrue(result.isThreat());
        assertEquals(SecurityAnalysisService.RiskLevel.CRITICAL, result.getRiskLevel());
        assertTrue(result.getThreatScore() >= 60);
    }

    @Test
    void testAnalyzeThreatReport_WithNegativeSentiment() {
        // Given
        String report = "Severe breach detected. Critical vulnerability exploited. " +
                       "Malware infection spreading rapidly.";
        String sentiment = "Very negative";

        // When
        SecurityAnalysisService.ThreatAnalysisResult result = 
            securityAnalysisService.analyzeThreatReport(report, sentiment);

        // Then
        assertNotNull(result);
        assertEquals(sentiment, result.getSentiment());
        assertTrue(result.isThreat());
        assertTrue(result.getSeverityScore() > 0);
        assertNotNull(result.getRecommendation());
        assertFalse(result.getRecommendation().isEmpty());
    }

    @Test
    void testAnalyzeThreatReport_WithPositiveSentiment() {
        // Given
        String report = "Security patch successfully deployed. All systems secure.";
        String sentiment = "Positive";

        // When
        SecurityAnalysisService.ThreatAnalysisResult result = 
            securityAnalysisService.analyzeThreatReport(report, sentiment);

        // Then
        assertNotNull(result);
        assertEquals(sentiment, result.getSentiment());
        assertNotNull(result.getRiskLevel());
        assertNotNull(result.getRecommendation());
    }

    @Test
    void testRiskLevel_Calculation() {
        // Test different threat levels
        String[] texts = {
            "Normal text",
            "Vulnerability detected",
            "Critical exploit and malware threat",
            "Zero-day ransomware attack with immediate threat and severe vulnerability"
        };

        for (String text : texts) {
            SecurityAnalysisService.SecurityAnalysisResult result = 
                securityAnalysisService.analyzeSecurityContent(text);
            assertNotNull(result.getRiskLevel());
        }
    }

    @Test
    void testSecurityAnalysisResult_Getters() {
        // Given
        SecurityAnalysisService.SecurityAnalysisResult result = 
            new SecurityAnalysisService.SecurityAnalysisResult(
                true, 
                SecurityAnalysisService.RiskLevel.HIGH, 
                75, 
                java.util.Arrays.asList("exploit", "malware")
            );

        // Then
        assertTrue(result.isThreat());
        assertEquals(SecurityAnalysisService.RiskLevel.HIGH, result.getRiskLevel());
        assertEquals(75, result.getThreatScore());
        assertEquals(2, result.getDetectedThreats().size());
    }
}
