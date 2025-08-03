package com.mriridescent.threatdetection.iris.service;

import com.mriridescent.threatdetection.iris.dto.AnalysisRequestDto;
import com.mriridescent.threatdetection.iris.dto.AnalysisResultDto;
import com.mriridescent.threatdetection.iris.model.entity.MLModel;
import com.mriridescent.threatdetection.model.User;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Service for Iris email threat analysis.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IrisAnalysisService {

    private final MLModelService mlModelService;

    // In-memory storage for demo purposes - would be replaced with actual database repository
    private final Map<Long, AnalysisResultDto> analysisResults = new ConcurrentHashMap<>();
    private Long nextId = 1L;

    @Value("${threatdetection.iris.threat-threshold:0.75}")
    private double threatThreshold;

    /**
     * Analyze an email for threats
     *
     * @param request Analysis request
     * @return Analysis result
     */
    @CircuitBreaker(name = "irisAnalysis", fallbackMethod = "analyzeFallback")
    public AnalysisResultDto analyzeEmail(AnalysisRequestDto request) {
        log.info("Analyzing email from {} with subject: {}", request.getSender(), request.getSubject());

        // Get active models for analysis
        List<MLModel> activeModels = mlModelService.getActiveModels();

        if (activeModels.isEmpty()) {
            log.warn("No active ML models found for email analysis");
            return buildDefaultAnalysisResult(request, 0.0, "No active models available");
        }

        // Simulate model processing
        Map<String, Double> modelScores = new HashMap<>();
        double totalScore = 0.0;

        for (MLModel model : activeModels) {
            // Simulate model-specific threat detection
            double score = simulateModelAnalysis(request, model);
            modelScores.put(model.getName(), score);
            totalScore += score * (model.getF1Score() / activeModels.size()); // Weight by model F1 score
        }

        // Determine overall threat score and level
        double threatScore = Math.min(1.0, Math.max(0.0, totalScore)); // Ensure between 0 and 1
        String threatLevel = determineThreatLevel(threatScore);

        // Create analysis result
        AnalysisResultDto result = AnalysisResultDto.builder()
                .id(nextId++)
                .emailSubject(request.getSubject())
                .emailSender(request.getSender())
                .threatScore(threatScore)
                .threatLevel(threatLevel)
                .detectedThreats(generateDetectedThreats(threatScore, request))
                .modelScores(modelScores)
                .flaggedElements(generateFlaggedElements(request))
                .metadataAnalysis(generateMetadataAnalysis(request))
                .headerAnalysis(generateHeaderAnalysis(request))
                .recommendedActions(generateRecommendedActions(threatScore))
                .falsePositiveFeedback(false)
                .analyzedAt(LocalDateTime.now())
                .analyzedBy(getCurrentUsername())
                .build();

        // Store result
        analysisResults.put(result.getId(), result);

        log.info("Email analysis completed with threat score: {} ({})", threatScore, threatLevel);
        return result;
    }

    /**
     * Fallback method for circuit breaker
     *
     * @param request Analysis request
     * @param ex Exception that triggered fallback
     * @return Default analysis result
     */
    public AnalysisResultDto analyzeFallback(AnalysisRequestDto request, Exception ex) {
        log.error("Fallback: Error analyzing email: {}", ex.getMessage());
        return buildDefaultAnalysisResult(request, 0.5, "Analysis system degraded, default assessment provided");
    }

    /**
     * Get recent analyses
     *
     * @param limit Maximum number of results to return
     * @return List of recent analysis results
     */
    public List<AnalysisResultDto> getRecentAnalyses(int limit) {
        log.info("Fetching {} recent analyses", limit);
        return analysisResults.values().stream()
                .sorted(Comparator.comparing(AnalysisResultDto::getAnalyzedAt).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Get analyses by minimum threat level
     *
     * @param minThreatLevel Minimum threat level
     * @param limit Maximum number of results to return
     * @return List of high-threat analysis results
     */
    public List<AnalysisResultDto> getAnalysesByMinThreatLevel(double minThreatLevel, int limit) {
        log.info("Fetching analyses with minimum threat level: {}", minThreatLevel);
        return analysisResults.values().stream()
                .filter(result -> result.getThreatScore() >= minThreatLevel)
                .sorted(Comparator.comparing(AnalysisResultDto::getThreatScore).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Submit feedback on analysis result
     *
     * @param analysisId Analysis ID
     * @param isThreat Whether the analysis correctly identified a threat
     * @return Updated analysis result
     */
    public AnalysisResultDto submitFeedback(Long analysisId, boolean isThreat) {
        log.info("Submitting feedback for analysis ID: {}, isThreat: {}", analysisId, isThreat);

        AnalysisResultDto result = analysisResults.get(analysisId);
        if (result == null) {
            throw new RuntimeException("Analysis not found with id: " + analysisId);
        }

        // Update the false positive feedback flag
        result.setFalsePositiveFeedback(!isThreat && result.getThreatScore() > threatThreshold);

        // In a real system, this feedback would be used to retrain models
        log.info("Feedback recorded for analysis ID: {}", analysisId);

        return result;
    }

    // Helper methods for simulating analysis

    private double simulateModelAnalysis(AnalysisRequestDto request, MLModel model) {
        // Simulate different model behaviors based on model type
        Random random = new Random(request.getSubject().hashCode() + request.getSender().hashCode());

        switch (model.getModelType()) {
            case NEURAL_NETWORK:
                // More sensitive to content
                return simulateContentAnalysis(request, random);

            case RANDOM_FOREST:
                // More sensitive to headers and metadata
                return simulateHeaderAnalysis(request, random);

            case GRADIENT_BOOSTING:
                // Balanced approach
                return (simulateContentAnalysis(request, random) + simulateHeaderAnalysis(request, random)) / 2.0;

            case SVM:
                // More sensitive to sender patterns
                return simulateSenderAnalysis(request, random);

            case ENSEMBLE:
                // Comprehensive analysis
                return (simulateContentAnalysis(request, random) * 0.4 +
                        simulateHeaderAnalysis(request, random) * 0.3 +
                        simulateSenderAnalysis(request, random) * 0.3);

            default:
                return random.nextDouble() * 0.5; // Default random score
        }
    }

    private double simulateContentAnalysis(AnalysisRequestDto request, Random random) {
        // Check for common phishing content patterns
        String lowercaseBody = request.getBody().toLowerCase();
        String lowercaseSubject = request.getSubject().toLowerCase();

        double score = 0.0;

        // Check for urgency language
        if (lowercaseSubject.contains("urgent") || 
            lowercaseSubject.contains("immediate") ||
            lowercaseSubject.contains("alert") ||
            lowercaseBody.contains("urgent action required")) {
            score += 0.2;
        }

        // Check for financial/sensitive information requests
        if (lowercaseBody.contains("password") ||
            lowercaseBody.contains("credit card") ||
            lowercaseBody.contains("bank account") ||
            lowercaseBody.contains("social security")) {
            score += 0.3;
        }

        // Check for suspicious links
        if (lowercaseBody.contains("click here") ||
            lowercaseBody.contains("verify your account") ||
            lowercaseBody.contains("login to your account")) {
            score += 0.25;
        }

        // Add some randomness to the score
        score += random.nextDouble() * 0.15;

        return Math.min(1.0, score);
    }

    private double simulateHeaderAnalysis(AnalysisRequestDto request, Random random) {
        double score = 0.0;

        // Check for mismatched sender domain
        if (request.getSender() != null && !request.getSender().isEmpty()) {
            String senderDomain = extractDomain(request.getSender());
            if (request.getHeaders() != null && request.getHeaders().containsKey("Return-Path")) {
                String returnPathDomain = extractDomain(request.getHeaders().get("Return-Path"));
                if (!returnPathDomain.equals(senderDomain)) {
                    score += 0.4;
                }
            }
        }

        // Check for suspicious mail server
        if (request.getMailServer() != null && !request.getMailServer().isEmpty()) {
            if (!isCommonMailServer(request.getMailServer())) {
                score += 0.15;
            }
        }

        // Add some randomness to the score
        score += random.nextDouble() * 0.2;

        return Math.min(1.0, score);
    }

    private double simulateSenderAnalysis(AnalysisRequestDto request, Random random) {
        double score = 0.0;

        // Check for suspicious sender domain
        if (request.getSender() != null && !request.getSender().isEmpty()) {
            String senderDomain = extractDomain(request.getSender());
            if (isSuspiciousDomain(senderDomain)) {
                score += 0.35;
            }
        }

        // Check for sender/recipient mismatch patterns
        if (request.getRecipients() != null && request.getRecipients().length > 0) {
            boolean allDifferentDomains = true;
            String senderDomain = extractDomain(request.getSender());

            for (String recipient : request.getRecipients()) {
                String recipientDomain = extractDomain(recipient);
                if (recipientDomain.equals(senderDomain)) {
                    allDifferentDomains = false;
                    break;
                }
            }

            if (allDifferentDomains) {
                score += 0.2;
            }
        }

        // Add some randomness to the score
        score += random.nextDouble() * 0.25;

        return Math.min(1.0, score);
    }

    private String extractDomain(String email) {
        if (email == null || !email.contains("@")) {
            return "";
        }
        return email.substring(email.indexOf('@') + 1).toLowerCase();
    }

    private boolean isCommonMailServer(String server) {
        String[] commonServers = {"gmail", "outlook", "hotmail", "yahoo", "aol", "icloud", "protonmail"};
        for (String commonServer : commonServers) {
            if (server.toLowerCase().contains(commonServer)) {
                return true;
            }
        }
        return false;
    }

    private boolean isSuspiciousDomain(String domain) {
        // Check for newly registered or uncommon TLDs often used in phishing
        String[] suspiciousTlds = {".xyz", ".top", ".club", ".online", ".site", ".icu", ".buzz"};
        for (String tld : suspiciousTlds) {
            if (domain.endsWith(tld)) {
                return true;
            }
        }

        // Check for domain typosquatting common patterns
        String[] commonDomains = {"google", "microsoft", "apple", "amazon", "paypal", "facebook", "bank"};
        for (String commonDomain : commonDomains) {
            if (domain.contains(commonDomain) && 
                (domain.contains("-") || domain.contains("0") || 
                 domain.contains("1") || domain.contains("_"))) {
                return true;
            }
        }

        return false;
    }

    private String determineThreatLevel(double threatScore) {
        if (threatScore >= 0.8) return "CRITICAL";
        if (threatScore >= 0.6) return "HIGH";
        if (threatScore >= 0.4) return "MEDIUM";
        if (threatScore >= 0.2) return "LOW";
        return "MINIMAL";
    }

    private List<String> generateDetectedThreats(double threatScore, AnalysisRequestDto request) {
        List<String> threats = new ArrayList<>();

        if (threatScore >= 0.2) {
            // Low-level threats
            if (request.getBody().toLowerCase().contains("click here")) {
                threats.add("Suspicious call to action");
            }
        }

        if (threatScore >= 0.4) {
            // Medium-level threats
            if (request.getSubject().toLowerCase().contains("urgent")) {
                threats.add("Urgency manipulation");
            }
            if (request.getBody().toLowerCase().contains("verify your account")) {
                threats.add("Account verification request");
            }
        }

        if (threatScore >= 0.6) {
            // High-level threats
            if (request.getBody().toLowerCase().contains("password") || 
                request.getBody().toLowerCase().contains("login")) {
                threats.add("Credential harvesting attempt");
            }
            if (isSuspiciousDomain(extractDomain(request.getSender()))) {
                threats.add("Sender domain suspicious");
            }
        }

        if (threatScore >= 0.8) {
            // Critical-level threats
            if (request.getBody().toLowerCase().contains("bank account") ||
                request.getBody().toLowerCase().contains("credit card")) {
                threats.add("Financial information request");
            }

            // Check for potential domain spoofing
            if (request.getSender() != null && request.getHeaders() != null && 
                request.getHeaders().containsKey("Return-Path")) {
                String senderDomain = extractDomain(request.getSender());
                String returnPathDomain = extractDomain(request.getHeaders().get("Return-Path"));
                if (!returnPathDomain.equals(senderDomain)) {
                    threats.add("Sender domain spoofing detected");
                }
            }
        }

        return threats;
    }

    private List<String> generateFlaggedElements(AnalysisRequestDto request) {
        List<String> flagged = new ArrayList<>();

        // Check for suspicious links (simplified simulation)
        if (request.getBody().contains("http://") || request.getBody().contains("https://")) {
            flagged.add("Embedded URL(s)");
        }

        // Check for suspicious attachments
        if (request.getAttachmentNames() != null && request.getAttachmentNames().length > 0) {
            for (String attachment : request.getAttachmentNames()) {
                if (attachment.endsWith(".exe") || attachment.endsWith(".zip") || 
                    attachment.endsWith(".js") || attachment.endsWith(".vbs")) {
                    flagged.add("Suspicious attachment: " + attachment);
                }
            }
        }

        // Check for suspicious subject line patterns
        if (request.getSubject().toUpperCase().equals(request.getSubject()) && request.getSubject().length() > 10) {
            flagged.add("All-caps subject line");
        }

        // Check for suspicious content patterns
        String lowerBody = request.getBody().toLowerCase();
        if (lowerBody.contains("urgent") && 
            (lowerBody.contains("bank") || lowerBody.contains("account") || 
             lowerBody.contains("password") || lowerBody.contains("verify"))) {
            flagged.add("Urgency combined with sensitive information request");
        }

        return flagged;
    }

    private Map<String, String> generateMetadataAnalysis(AnalysisRequestDto request) {
        Map<String, String> analysis = new HashMap<>();

        // Sender analysis
        analysis.put("senderDomain", extractDomain(request.getSender()));
        analysis.put("senderTrustScore", String.format("%.2f", calculateSenderTrustScore(request.getSender())));

        // Client IP analysis (if available)
        if (request.getClientIp() != null && !request.getClientIp().isEmpty()) {
            analysis.put("ipLocation", simulateIpGeolocation(request.getClientIp()));
            analysis.put("ipReputation", calculateIpReputation(request.getClientIp()));
        }

        // Message structure analysis
        analysis.put("recipientCount", String.valueOf(request.getRecipients().length));
        if (request.getAttachmentNames() != null) {
            analysis.put("attachmentCount", String.valueOf(request.getAttachmentNames().length));
        }

        return analysis;
    }

    private Map<String, String> generateHeaderAnalysis(AnalysisRequestDto request) {
        Map<String, String> analysis = new HashMap<>();

        if (request.getHeaders() != null && !request.getHeaders().isEmpty()) {
            // SPF check simulation
            analysis.put("spfCheck", simulateSpfCheck(request));

            // DKIM check simulation
            analysis.put("dkimCheck", simulateDkimCheck(request));

            // DMARC check simulation
            analysis.put("dmarcCheck", simulateDmarcCheck(request));

            // Received headers analysis
            analysis.put("receivedHeadersAnalysis", "Normal routing pattern detected");

            // Message ID analysis
            if (request.getHeaders().containsKey("Message-ID")) {
                String messageId = request.getHeaders().get("Message-ID");
                String domain = extractDomainFromMessageId(messageId);
                if (!domain.equals(extractDomain(request.getSender()))) {
                    analysis.put("messageIdAnalysis", "Message-ID domain mismatch with sender");
                } else {
                    analysis.put("messageIdAnalysis", "Message-ID consistent with sender");
                }
            }
        }

        return analysis;
    }

    private List<String> generateRecommendedActions(double threatScore) {
        List<String> actions = new ArrayList<>();

        if (threatScore < 0.2) {
            actions.add("No action required");
        } else if (threatScore < 0.4) {
            actions.add("Exercise normal caution");
            actions.add("Do not share sensitive information");
        } else if (threatScore < 0.6) {
            actions.add("Verify sender through alternate channel before responding");
            actions.add("Do not click links or open attachments");
            actions.add("Report to IT security team");
        } else if (threatScore < 0.8) {
            actions.add("Do not respond to this email");
            actions.add("Report to IT security team immediately");
            actions.add("Block sender in email client");
            actions.add("Scan system if links were clicked or attachments opened");
        } else {
            actions.add("Critical threat - Do not interact with this email in any way");
            actions.add("Quarantine email");
            actions.add("Alert IT security team immediately");
            actions.add("If any information was shared, take immediate remediation steps");
            actions.add("Run full system security scan");
        }

        return actions;
    }

    private double calculateSenderTrustScore(String sender) {
        if (sender == null || sender.isEmpty()) {
            return 0.0;
        }

        String domain = extractDomain(sender);

        // Simulate higher trust for common domains
        String[] trustedDomains = {"gmail.com", "outlook.com", "hotmail.com", "yahoo.com", "aol.com", "icloud.com"};
        for (String trustedDomain : trustedDomains) {
            if (domain.equals(trustedDomain)) {
                return 0.8 + (new Random().nextDouble() * 0.2);
            }
        }

        // Corporate domains often have higher trust
        if (domain.length() > 8 && !domain.contains("-") && !domain.contains("0") && !domain.contains("1")) {
            return 0.6 + (new Random().nextDouble() * 0.3);
        }

        // Default moderate-to-low trust for unknown domains
        return 0.3 + (new Random().nextDouble() * 0.3);
    }

    private String simulateIpGeolocation(String ip) {
        // Simplified simulation - in a real system this would use a geolocation service
        String[] countries = {"United States", "Germany", "France", "United Kingdom", "Russia", "China", "Brazil", "India"};
        int ipSum = 0;
        for (char c : ip.toCharArray()) {
            ipSum += c;
        }
        return countries[ipSum % countries.length];
    }

    private String calculateIpReputation(String ip) {
        // Simplified simulation - in a real system this would check reputation databases
        int ipSum = 0;
        for (char c : ip.toCharArray()) {
            ipSum += c;
        }

        int score = (ipSum % 100) + 1; // 1-100 score

        if (score > 90) return "Excellent";
        if (score > 75) return "Good";
        if (score > 50) return "Fair";
        if (score > 25) return "Poor";
        return "Very Poor";
    }

    private String simulateSpfCheck(AnalysisRequestDto request) {
        // Simplified SPF check simulation
        if (request.getHeaders() == null || !request.getHeaders().containsKey("Received-SPF")) {
            return "No SPF record found";
        }

        String spfHeader = request.getHeaders().get("Received-SPF").toLowerCase();
        if (spfHeader.contains("pass")) {
            return "Pass";
        } else if (spfHeader.contains("fail")) {
            return "Fail";
        } else if (spfHeader.contains("softfail")) {
            return "SoftFail";
        } else if (spfHeader.contains("neutral")) {
            return "Neutral";
        }

        return "Unknown";
    }

    private String simulateDkimCheck(AnalysisRequestDto request) {
        // Simplified DKIM check simulation
        if (request.getHeaders() == null || !request.getHeaders().containsKey("DKIM-Signature")) {
            return "No DKIM signature found";
        }

        // Simulate verification result based on sender domain
        String domain = extractDomain(request.getSender());
        if (domain.contains("gmail") || domain.contains("outlook") || domain.contains("yahoo")) {
            return "Valid";
        }

        return new Random().nextDouble() > 0.3 ? "Valid" : "Invalid";
    }

    private String simulateDmarcCheck(AnalysisRequestDto request) {
        // Simplified DMARC check simulation
        if (request.getHeaders() == null) {
            return "No DMARC record found";
        }

        // Check alignment between From domain and SPF/DKIM
        String domain = extractDomain(request.getSender());
        String spfResult = simulateSpfCheck(request);
        String dkimResult = simulateDkimCheck(request);

        if (spfResult.equals("Pass") || dkimResult.equals("Valid")) {
            // Simulate domain having a DMARC policy
            if (domain.contains("gmail") || domain.contains("outlook") || domain.contains("yahoo")) {
                return "Pass";
            }

            return new Random().nextDouble() > 0.4 ? "Pass" : "Fail";
        }

        return "Fail";
    }

    private String extractDomainFromMessageId(String messageId) {
        if (messageId == null || !messageId.contains("@")) {
            return "";
        }
        String domain = messageId.substring(messageId.lastIndexOf('@') + 1);
        // Remove any closing angle bracket and trim
        return domain.replace(">", "").trim();
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return ((User) authentication.getPrincipal()).getUsername();
        }
        return "system";
    }

    private AnalysisResultDto buildDefaultAnalysisResult(AnalysisRequestDto request, double score, String message) {
        return AnalysisResultDto.builder()
                .id(nextId++)
                .emailSubject(request.getSubject())
                .emailSender(request.getSender())
                .threatScore(score)
                .threatLevel(determineThreatLevel(score))
                .detectedThreats(List.of(message))
                .modelScores(new HashMap<>())
                .flaggedElements(new ArrayList<>())
                .metadataAnalysis(new HashMap<>())
                .headerAnalysis(new HashMap<>())
                .recommendedActions(List.of("Review manually"))
                .falsePositiveFeedback(false)
                .analyzedAt(LocalDateTime.now())
                .analyzedBy(getCurrentUsername())
                .build();
    }
}
