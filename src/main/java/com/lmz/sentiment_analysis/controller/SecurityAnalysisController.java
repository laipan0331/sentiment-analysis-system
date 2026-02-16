package com.lmz.sentiment_analysis.controller;

import com.lmz.sentiment_analysis.service.HybridNLPProcessor;
import com.lmz.sentiment_analysis.service.SecurityAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for security-related sentiment analysis.
 * Provides endpoints for analyzing security threats and threat intelligence reports.
 */
@RestController
@RequestMapping("/api/security")
public class SecurityAnalysisController {

    private final SecurityAnalysisService securityAnalysisService;
    private final HybridNLPProcessor hybridNLPProcessor;

    @Autowired
    public SecurityAnalysisController(SecurityAnalysisService securityAnalysisService,
                                     HybridNLPProcessor hybridNLPProcessor) {
        this.securityAnalysisService = securityAnalysisService;
        this.hybridNLPProcessor = hybridNLPProcessor;
    }

    /**
     * Analyze text for security threats and malicious content.
     * 
     * @param request Request body containing text to analyze
     * @return Security analysis result
     */
    @PostMapping("/analyze")
    public ResponseEntity<Map<String, Object>> analyzeSecurityContent(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        
        if (text == null || text.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Text content is required"));
        }

        SecurityAnalysisService.SecurityAnalysisResult result = 
            securityAnalysisService.analyzeSecurityContent(text);

        Map<String, Object> response = new HashMap<>();
        response.put("isThreat", result.isThreat());
        response.put("riskLevel", result.getRiskLevel().toString());
        response.put("threatScore", result.getThreatScore());
        response.put("detectedThreats", result.getDetectedThreats());

        return ResponseEntity.ok(response);
    }

    /**
     * Analyze threat intelligence report with sentiment and security analysis.
     * 
     * @param request Request body containing threat report
     * @return Comprehensive threat analysis result
     */
    @PostMapping("/analyze-threat-report")
    public ResponseEntity<Map<String, Object>> analyzeThreatReport(@RequestBody Map<String, String> request) {
        String report = request.get("report");
        
        if (report == null || report.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Report content is required"));
        }

        // First analyze sentiment
        HybridNLPProcessor.SentimentResult sentimentResult = 
            hybridNLPProcessor.analyzeSentiment(report);

        // Then perform security analysis
        SecurityAnalysisService.ThreatAnalysisResult threatResult = 
            securityAnalysisService.analyzeThreatReport(report, sentimentResult.getSentiment());

        Map<String, Object> response = new HashMap<>();
        response.put("sentiment", threatResult.getSentiment());
        response.put("sentimentConfidence", sentimentResult.getConfidence());
        response.put("sentimentSource", sentimentResult.getSource());
        response.put("riskLevel", threatResult.getRiskLevel().toString());
        response.put("severityScore", threatResult.getSeverityScore());
        response.put("isThreat", threatResult.isThreat());
        response.put("detectedThreats", threatResult.getDetectedThreats());
        response.put("recommendation", threatResult.getRecommendation());

        return ResponseEntity.ok(response);
    }

    /**
     * Get security analysis statistics.
     * 
     * @return Security metrics and statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getSecurityStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("message", "Security analysis service is active");
        stats.put("features", new String[]{
            "Malicious content detection",
            "Threat intelligence analysis",
            "Risk level assessment",
            "Security sentiment analysis"
        });
        
        return ResponseEntity.ok(stats);
    }
}
