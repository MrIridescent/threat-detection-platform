package com.mriridescent.threatdetection.iris.service;

import com.mriridescent.threatdetection.iris.model.entity.EmailFeature;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service for extracting features from emails for ML analysis.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FeatureExtractionService {

    // Common phishing keywords
    private static final List<String> SUSPICIOUS_KEYWORDS = List.of(
            "urgent", "verify", "account", "suspended", "update", "confirm",
            "security", "unusual", "access", "click", "link", "password",
            "information", "bank", "credit", "debit", "login", "signin",
            "authenticate", "validate", "immediately", "required"
    );

    // URL and IP regex patterns
    private static final Pattern URL_PATTERN = 
            Pattern.compile("https?://[\\w\\.-]+\.\\w+(/[\\w\\.-]*)*");
    private static final Pattern IP_URL_PATTERN = 
            Pattern.compile("https?://\\d{1,3}\.\\d{1,3}\.\\d{1,3}\.\\d{1,3}");

    /**
     * Extracts features from an email for ML analysis.
     *
     * @param sender    The email sender
     * @param recipient The email recipient
     * @param subject   The email subject
     * @param content   The email content
     * @param metadata  Additional email metadata
     * @return List of extracted features
     */
    public List<EmailFeature> extractFeatures(String sender, String recipient, 
                                             String subject, String content,
                                             Map<String, String> metadata) {
        List<EmailFeature> features = new ArrayList<>();

        // Extract header features
        features.addAll(extractHeaderFeatures(sender, recipient, subject));

        // Extract content features
        features.addAll(extractContentFeatures(content));

        // Extract metadata features
        if (metadata != null) {
            features.addAll(extractMetadataFeatures(metadata));
        }

        // Extract network features (URLs, domains, etc.)
        features.addAll(extractNetworkFeatures(content));

        // Analyze semantic patterns
        features.addAll(extractSemanticFeatures(subject, content));

        return features;
    }

    /**
     * Extracts features from email headers.
     */
    private List<EmailFeature> extractHeaderFeatures(String sender, String recipient, String subject) {
        List<EmailFeature> features = new ArrayList<>();

        // Sender domain analysis
        String senderDomain = extractDomain(sender);
        features.add(createFeature("sender_domain", senderDomain, 0.7, EmailFeature.FeatureType.HEADER));

        // Check for sender display name vs email mismatch
        if (sender.contains("<") && sender.contains(">")) {
            String displayName = sender.substring(0, sender.indexOf('<')).trim();
            String email = sender.substring(sender.indexOf('<') + 1, sender.indexOf('>')).trim();
            boolean mismatch = !email.toLowerCase().contains(displayName.toLowerCase()) && 
                             displayName.length() > 3;
            features.add(createFeature("display_name_mismatch", String.valueOf(mismatch), 
                                     mismatch ? 0.8 : 0.1, EmailFeature.FeatureType.HEADER));
        }

        // Subject line analysis
        boolean subjectHasUrgency = SUSPICIOUS_KEYWORDS.stream()
                .anyMatch(keyword -> subject.toLowerCase().contains(keyword));
        features.add(createFeature("subject_urgency", String.valueOf(subjectHasUrgency), 
                                 subjectHasUrgency ? 0.6 : 0.2, EmailFeature.FeatureType.HEADER));

        // Subject has excessive punctuation
        long exclamationCount = subject.chars().filter(ch -> ch == '!').count();
        boolean excessivePunctuation = exclamationCount > 1;
        features.add(createFeature("subject_excessive_punctuation", String.valueOf(excessivePunctuation), 
                                 excessivePunctuation ? 0.5 : 0.1, EmailFeature.FeatureType.HEADER));

        // Subject length (short subjects are sometimes suspicious)
        boolean shortSubject = subject.length() < 10;
        features.add(createFeature("short_subject", String.valueOf(shortSubject), 
                                 shortSubject ? 0.4 : 0.1, EmailFeature.FeatureType.HEADER));

        return features;
    }

    /**
     * Extracts features from email content.
     */
    private List<EmailFeature> extractContentFeatures(String content) {
        List<EmailFeature> features = new ArrayList<>();

        // Check for suspicious keywords in content
        long suspiciousKeywordCount = SUSPICIOUS_KEYWORDS.stream()
                .filter(keyword -> content.toLowerCase().contains(keyword))
                .count();
        double keywordDensity = (double) suspiciousKeywordCount / SUSPICIOUS_KEYWORDS.size();
        features.add(createFeature("suspicious_keyword_density", String.format("%.2f", keywordDensity), 
                                 keywordDensity * 0.7, EmailFeature.FeatureType.CONTENT));

        // Check for password or credential requests
        boolean asksForCredentials = content.toLowerCase().contains("password") || 
                                   content.toLowerCase().contains("login") || 
                                   content.toLowerCase().contains("credential");
        features.add(createFeature("asks_for_credentials", String.valueOf(asksForCredentials), 
                                 asksForCredentials ? 0.9 : 0.1, EmailFeature.FeatureType.CONTENT));

        // Check for HTML forms
        boolean containsForm = content.toLowerCase().contains("<form") && 
                              content.toLowerCase().contains("</form>");
        features.add(createFeature("contains_form", String.valueOf(containsForm), 
                                 containsForm ? 0.8 : 0.1, EmailFeature.FeatureType.CONTENT));

        // Check for obfuscated links (href doesn't match text)
        boolean hasObfuscatedLinks = checkForObfuscatedLinks(content);
        features.add(createFeature("obfuscated_links", String.valueOf(hasObfuscatedLinks), 
                                 hasObfuscatedLinks ? 0.9 : 0.1, EmailFeature.FeatureType.CONTENT));

        // Message tone analysis (urgency, threat, etc.)
        boolean urgentTone = content.toLowerCase().contains("urgent") || 
                            content.toLowerCase().contains("immediately") || 
                            content.toLowerCase().contains("alert");
        features.add(createFeature("urgent_tone", String.valueOf(urgentTone), 
                                 urgentTone ? 0.7 : 0.2, EmailFeature.FeatureType.CONTENT));

        return features;
    }

    /**
     * Extracts features from email metadata.
     */
    private List<EmailFeature> extractMetadataFeatures(Map<String, String> metadata) {
        List<EmailFeature> features = new ArrayList<>();

        // Check for spoofed headers
        if (metadata.containsKey("authentication-results")) {
            String authResults = metadata.get("authentication-results");
            boolean spfFailed = authResults.contains("spf=fail");
            boolean dkimFailed = authResults.contains("dkim=fail");

            features.add(createFeature("spf_failed", String.valueOf(spfFailed), 
                                     spfFailed ? 0.8 : 0.2, EmailFeature.FeatureType.METADATA));
            features.add(createFeature("dkim_failed", String.valueOf(dkimFailed), 
                                     dkimFailed ? 0.8 : 0.2, EmailFeature.FeatureType.METADATA));
        }

        // Check for unusual sending servers or routes
        if (metadata.containsKey("received")) {
            String receivedChain = metadata.get("received");
            boolean unusualRoute = checkForUnusualRoute(receivedChain);
            features.add(createFeature("unusual_routing", String.valueOf(unusualRoute), 
                                     unusualRoute ? 0.7 : 0.1, EmailFeature.FeatureType.METADATA));
        }

        // Check for newly registered sending domain
        if (metadata.containsKey("sender_domain_age_days")) {
            int domainAgeDays = Integer.parseInt(metadata.get("sender_domain_age_days"));
            boolean newDomain = domainAgeDays < 30; // Less than 30 days old
            features.add(createFeature("new_domain", String.valueOf(newDomain), 
                                     newDomain ? 0.8 : 0.2, EmailFeature.FeatureType.METADATA));
        }

        return features;
    }

    /**
     * Extracts network-related features from email content.
     */
    private List<EmailFeature> extractNetworkFeatures(String content) {
        List<EmailFeature> features = new ArrayList<>();

        // Extract URLs from content
        List<String> urls = extractUrls(content);

        // Check for IP-based URLs (suspicious)
        boolean hasIpUrls = urls.stream().anyMatch(url -> IP_URL_PATTERN.matcher(url).matches());
        features.add(createFeature("ip_based_urls", String.valueOf(hasIpUrls), 
                                 hasIpUrls ? 0.9 : 0.1, EmailFeature.FeatureType.NETWORK));

        // Check for URL shorteners
        boolean hasUrlShorteners = urls.stream().anyMatch(this::isUrlShortener);
        features.add(createFeature("url_shorteners", String.valueOf(hasUrlShorteners), 
                                 hasUrlShorteners ? 0.8 : 0.1, EmailFeature.FeatureType.NETWORK));

        // Check for domain age mismatches (if available from metadata)
        // This would require external API calls in a real implementation

        // Check for mismatched link text and destinations
        boolean mismatchedLinks = checkForMismatchedLinks(content);
        features.add(createFeature("mismatched_links", String.valueOf(mismatchedLinks), 
                                 mismatchedLinks ? 0.8 : 0.1, EmailFeature.FeatureType.NETWORK));

        return features;
    }

    /**
     * Extracts semantic features from email content.
     */
    private List<EmailFeature> extractSemanticFeatures(String subject, String content) {
        List<EmailFeature> features = new ArrayList<>();

        // Analyze for fear/urgency tactics
        boolean usesFearTactics = checkForFearTactics(subject, content);
        features.add(createFeature("fear_tactics", String.valueOf(usesFearTactics), 
                                 usesFearTactics ? 0.8 : 0.2, EmailFeature.FeatureType.SEMANTIC));

        // Analyze for reward/enticement tactics
        boolean usesRewardTactics = checkForRewardTactics(subject, content);
        features.add(createFeature("reward_tactics", String.valueOf(usesRewardTactics), 
                                 usesRewardTactics ? 0.7 : 0.2, EmailFeature.FeatureType.SEMANTIC));

        // Analyze for authority impersonation
        boolean impersonatesAuthority = checkForAuthorityImpersonation(subject, content);
        features.add(createFeature("authority_impersonation", String.valueOf(impersonatesAuthority), 
                                 impersonatesAuthority ? 0.9 : 0.1, EmailFeature.FeatureType.SEMANTIC));

        // Analyze for poor grammar/spelling (indicator of phishing)
        boolean poorLanguageQuality = checkForPoorLanguageQuality(content);
        features.add(createFeature("poor_language_quality", String.valueOf(poorLanguageQuality), 
                                 poorLanguageQuality ? 0.6 : 0.2, EmailFeature.FeatureType.SEMANTIC));

        return features;
    }

    /**
     * Creates a new feature with the given parameters.
     */
    private EmailFeature createFeature(String name, String value, double weight, EmailFeature.FeatureType type) {
        return EmailFeature.builder()
                .featureName(name)
                .featureValue(value)
                .weight(weight)
                .featureType(type)
                .build();
    }

    /**
     * Extracts a domain from an email address.
     */
    private String extractDomain(String emailAddress) {
        if (emailAddress.contains("@")) {
            String domain = emailAddress.substring(emailAddress.lastIndexOf("@") + 1);
            if (domain.contains(">")) {
                domain = domain.substring(0, domain.indexOf(">"));
            }
            return domain.toLowerCase();
        }
        return emailAddress; // Not an email or unable to parse
    }

    /**
     * Checks if a URL is from a known URL shortener service.
     */
    private boolean isUrlShortener(String url) {
        List<String> shortenerDomains = List.of(
                "bit.ly", "tinyurl.com", "goo.gl", "t.co", "ow.ly", "is.gd", 
                "buff.ly", "adf.ly", "j.mp", "tr.im", "cli.gs", "rebrand.ly"
        );

        try {
            URI uri = new URI(url);
            String host = uri.getHost().toLowerCase();
            return shortenerDomains.stream().anyMatch(host::contains);
        } catch (URISyntaxException e) {
            return false;
        }
    }

    /**
     * Extracts URLs from content.
     */
    private List<String> extractUrls(String content) {
        List<String> urls = new ArrayList<>();
        Matcher matcher = URL_PATTERN.matcher(content);

        while (matcher.find()) {
            urls.add(matcher.group());
        }

        return urls;
    }

    /**
     * Checks for obfuscated links in HTML content.
     */
    private boolean checkForObfuscatedLinks(String content) {
        // Simplified implementation - would be more robust in production
        Pattern pattern = Pattern.compile("<a\\s+[^>]*href=\"([^\"]*)\"[^>]*>(.*?)</a>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            String href = matcher.group(1);
            String linkText = matcher.group(2).replaceAll("<[^>]*>", ""); // Remove any nested HTML

            // If the link text looks like a URL but doesn't match the href
            if (linkText.contains("http") && !href.contains(linkText) && 
                !linkText.contains(href)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks for mismatched link text and destinations.
     */
    private boolean checkForMismatchedLinks(String content) {
        // Similar to obfuscated links but with more specific checks
        Pattern pattern = Pattern.compile("<a\\s+[^>]*href=\"([^\"]*)\"[^>]*>(.*?)</a>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            String href = matcher.group(1).toLowerCase();
            String linkText = matcher.group(2).replaceAll("<[^>]*>", "").toLowerCase();

            // Check for brand names in text but not in URL
            List<String> commonBrands = List.of("paypal", "amazon", "microsoft", "apple", 
                                              "google", "facebook", "bank", "netflix");

            for (String brand : commonBrands) {
                if (linkText.contains(brand) && !extractDomain(href).contains(brand)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks for unusual email routing patterns.
     */
    private boolean checkForUnusualRoute(String receivedChain) {
        // Simplified implementation
        // Would analyze the received headers chain for suspicious patterns
        return receivedChain.contains("unknown") || 
               receivedChain.contains("suspicious") || 
               receivedChain.contains("unverified");
    }

    /**
     * Checks for fear-based manipulation tactics.
     */
    private boolean checkForFearTactics(String subject, String content) {
        String combined = (subject + " " + content).toLowerCase();
        List<String> fearKeywords = List.of(
                "unauthorized", "suspicious", "security", "alert", "warning", "blocked",
                "suspended", "locked", "unusual", "illegal", "fraud", "breach", "compromise",
                "urgently", "immediately", "important", "critical", "serious", "risk",
                "terminate", "cancel", "delete", "permanent", "never"
        );

        long matchCount = fearKeywords.stream()
                .filter(combined::contains)
                .count();

        return matchCount >= 3; // If 3 or more fear keywords are found
    }

    /**
     * Checks for reward-based manipulation tactics.
     */
    private boolean checkForRewardTactics(String subject, String content) {
        String combined = (subject + " " + content).toLowerCase();
        List<String> rewardKeywords = List.of(
                "free", "win", "winner", "congratulations", "selected", "exclusive",
                "prize", "gift", "bonus", "discount", "offer", "special", "limited",
                "promotion", "reward", "claim", "opportunity", "lucky", "chosen", "million"
        );

        long matchCount = rewardKeywords.stream()
                .filter(combined::contains)
                .count();

        return matchCount >= 3; // If 3 or more reward keywords are found
    }

    /**
     * Checks for authority impersonation tactics.
     */
    private boolean checkForAuthorityImpersonation(String subject, String content) {
        String combined = (subject + " " + content).toLowerCase();
        List<String> authorityTerms = List.of(
                "bank", "paypal", "amazon", "ebay", "apple", "microsoft", "google",
                "facebook", "instagram", "irs", "tax", "government", "official", "support",
                "service", "team", "security", "admin", "administrator", "department",
                "helpdesk", "it department", "customer service", "notification"
        );

        return authorityTerms.stream().anyMatch(combined::contains);
    }

    /**
     * Checks for poor language quality (grammar, spelling).
     */
    private boolean checkForPoorLanguageQuality(String content) {
        // Simplified implementation
        // In production, would use NLP libraries for grammar/spelling analysis

        // Check for common mistakes
        List<String> poorQualityIndicators = List.of(
                "kindly", "dear customer", "dear user", "valued customer",
                "your account will", "verify your", "click here", "click below",
                "please confirm your", "please verify", "urgent action"
        );

        long matchCount = poorQualityIndicators.stream()
                .filter(indicator -> content.toLowerCase().contains(indicator))
                .count();

        return matchCount >= 2;
    }
}
