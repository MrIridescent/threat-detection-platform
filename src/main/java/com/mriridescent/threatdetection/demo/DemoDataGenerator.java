package com.mriridescent.threatdetection.demo;

import com.mriridescent.threatdetection.phishnet.model.entity.PhishingCampaign;
import com.mriridescent.threatdetection.phishnet.model.entity.InfrastructureNode;
import com.mriridescent.threatdetection.phishnet.repository.PhishingCampaignRepository;
import com.mriridescent.threatdetection.phishnet.repository.InfrastructureNodeRepository;
import com.mriridescent.threatdetection.iris.model.entity.EmailAnalysis;
import com.mriridescent.threatdetection.iris.model.entity.MLModel;
import com.mriridescent.threatdetection.iris.repository.EmailAnalysisRepository;
import com.mriridescent.threatdetection.iris.repository.MLModelRepository;
import com.mriridescent.threatdetection.model.entity.User;
import com.mriridescent.threatdetection.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Generates realistic demo data for showcasing the threat detection platform
 */
@Component
@Profile({"demo", "dev"})
@RequiredArgsConstructor
@Slf4j
public class DemoDataGenerator implements CommandLineRunner {

    private final PhishingCampaignRepository campaignRepository;
    private final InfrastructureNodeRepository nodeRepository;
    private final EmailAnalysisRepository emailAnalysisRepository;
    private final MLModelRepository mlModelRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final Random random = new Random();

    @Override
    @Transactional
    public void run(String... args) {
        if (campaignRepository.count() > 0) {
            log.info("Demo data already exists, skipping generation");
            return;
        }

        log.info("🎭 Generating realistic demo data for threat detection platform...");

        // Create demo users
        createDemoUsers();

        // Create ML models
        createMLModels();

        // Generate phishing campaigns
        List<PhishingCampaign> campaigns = generatePhishingCampaigns();

        // Generate infrastructure networks
        generateInfrastructureNetworks(campaigns);

        // Generate email analyses
        generateEmailAnalyses();

        log.info("✅ Demo data generation completed successfully!");
        log.info("📊 Generated {} campaigns with realistic threat patterns", campaigns.size());
    }

    private void createDemoUsers() {
        log.info("Creating demo users...");

        List<User> users = Arrays.asList(
                User.builder()
                        .username("admin")
                        .email("admin@threatdetection.ai")
                        .password(passwordEncoder.encode("admin123"))
                        .role(User.Role.ADMIN)
                        .enabled(true)
                        .build(),
                User.builder()
                        .username("analyst")
                        .email("analyst@threatdetection.ai")
                        .password(passwordEncoder.encode("analyst123"))
                        .role(User.Role.ANALYST)
                        .enabled(true)
                        .build(),
                User.builder()
                        .username("demo")
                        .email("demo@threatdetection.ai")
                        .password(passwordEncoder.encode("demo123"))
                        .role(User.Role.USER)
                        .enabled(true)
                        .build()
        );

        userRepository.saveAll(users);
        log.info("Created {} demo users", users.size());
    }

    private void createMLModels() {
        log.info("Creating ML models...");

        List<MLModel> models = Arrays.asList(
                MLModel.builder()
                        .name("PhishNet Neural Classifier v2.1")
                        .version("2.1.0")
                        .modelType(MLModel.ModelType.NEURAL_NETWORK)
                        .description("Deep neural network for phishing email classification")
                        .filePath("/models/phishnet_neural_v2.1.h5")
                        .accuracy(0.94)
                        .precision(0.92)
                        .recall(0.96)
                        .f1Score(0.94)
                        .active(true)
                        .lastTrainedAt(LocalDateTime.now().minusDays(7))
                        .build(),
                MLModel.builder()
                        .name("Campaign Evolution Predictor")
                        .version("1.3.2")
                        .modelType(MLModel.ModelType.GRADIENT_BOOSTING)
                        .description("XGBoost model for predicting campaign evolution patterns")
                        .filePath("/models/campaign_evolution_v1.3.pkl")
                        .accuracy(0.87)
                        .precision(0.85)
                        .recall(0.89)
                        .f1Score(0.87)
                        .active(true)
                        .lastTrainedAt(LocalDateTime.now().minusDays(3))
                        .build(),
                MLModel.builder()
                        .name("Infrastructure Anomaly Detector")
                        .version("1.0.5")
                        .modelType(MLModel.ModelType.SVM)
                        .description("Support Vector Machine for detecting anomalous infrastructure patterns")
                        .filePath("/models/infra_anomaly_v1.0.pkl")
                        .accuracy(0.91)
                        .precision(0.88)
                        .recall(0.94)
                        .f1Score(0.91)
                        .active(true)
                        .lastTrainedAt(LocalDateTime.now().minusDays(5))
                        .build(),
                MLModel.builder()
                        .name("Threat Attribution Ensemble")
                        .version("2.0.1")
                        .modelType(MLModel.ModelType.ENSEMBLE)
                        .description("Ensemble model combining multiple algorithms for threat actor attribution")
                        .filePath("/models/attribution_ensemble_v2.0.pkl")
                        .accuracy(0.89)
                        .precision(0.91)
                        .recall(0.87)
                        .f1Score(0.89)
                        .active(true)
                        .lastTrainedAt(LocalDateTime.now().minusDays(1))
                        .build()
        );

        mlModelRepository.saveAll(models);
        log.info("Created {} ML models", models.size());
    }

    private List<PhishingCampaign> generatePhishingCampaigns() {
        log.info("Generating phishing campaigns...");

        List<PhishingCampaign> campaigns = new ArrayList<>();

        // Campaign 1: Advanced Persistent Threat (APT)
        campaigns.add(PhishingCampaign.builder()
                .name("Operation Digital Mirage")
                .description("Sophisticated APT campaign targeting financial institutions with advanced social engineering and zero-day exploits")
                .status(PhishingCampaign.CampaignStatus.ACTIVE)
                .threatLevel(PhishingCampaign.ThreatLevel.CRITICAL)
                .targetSector("Financial Services")
                .discoveredAt(LocalDateTime.now().minusDays(15))
                .estimatedVictims(2847)
                .geographicScope("Global")
                .attackVectors(Arrays.asList("Spear Phishing", "Watering Hole", "Supply Chain"))
                .build());

        // Campaign 2: Business Email Compromise
        campaigns.add(PhishingCampaign.builder()
                .name("Executive Impersonation Wave")
                .description("Large-scale BEC campaign impersonating C-level executives to steal credentials and initiate fraudulent transfers")
                .status(PhishingCampaign.CampaignStatus.ESCALATING)
                .threatLevel(PhishingCampaign.ThreatLevel.HIGH)
                .targetSector("Technology")
                .discoveredAt(LocalDateTime.now().minusDays(8))
                .estimatedVictims(1203)
                .geographicScope("North America, Europe")
                .attackVectors(Arrays.asList("Email Spoofing", "Social Engineering", "Domain Squatting"))
                .build());

        // Campaign 3: Credential Harvesting
        campaigns.add(PhishingCampaign.builder()
                .name("CloudCred Harvester")
                .description("Targeted credential harvesting campaign focusing on cloud service accounts with sophisticated landing pages")
                .status(PhishingCampaign.CampaignStatus.ACTIVE)
                .threatLevel(PhishingCampaign.ThreatLevel.HIGH)
                .targetSector("Healthcare")
                .discoveredAt(LocalDateTime.now().minusDays(22))
                .estimatedVictims(5621)
                .geographicScope("Global")
                .attackVectors(Arrays.asList("Phishing", "Credential Stuffing", "Session Hijacking"))
                .build());

        // Campaign 4: Ransomware Distribution
        campaigns.add(PhishingCampaign.builder()
                .name("CryptoLock Distribution Network")
                .description("Multi-stage ransomware distribution campaign using compromised websites and malicious email attachments")
                .status(PhishingCampaign.CampaignStatus.MITIGATED)
                .threatLevel(PhishingCampaign.ThreatLevel.CRITICAL)
                .targetSector("Manufacturing")
                .discoveredAt(LocalDateTime.now().minusDays(45))
                .resolvedAt(LocalDateTime.now().minusDays(12))
                .estimatedVictims(892)
                .geographicScope("Asia-Pacific")
                .attackVectors(Arrays.asList("Malicious Attachments", "Drive-by Downloads", "RDP Exploitation"))
                .build());

        // Campaign 5: Mobile-Focused Attack
        campaigns.add(PhishingCampaign.builder()
                .name("Mobile Banking Trojan Campaign")
                .description("Sophisticated mobile malware campaign targeting banking applications with SMS-based social engineering")
                .status(PhishingCampaign.CampaignStatus.MONITORING)
                .threatLevel(PhishingCampaign.ThreatLevel.MEDIUM)
                .targetSector("Banking")
                .discoveredAt(LocalDateTime.now().minusDays(30))
                .estimatedVictims(3456)
                .geographicScope("Europe, Middle East")
                .attackVectors(Arrays.asList("SMS Phishing", "Malicious Apps", "SIM Swapping"))
                .build());

        // Campaign 6: Supply Chain Attack
        campaigns.add(PhishingCampaign.builder()
                .name("DevOps Pipeline Infiltration")
                .description("Advanced supply chain attack targeting software development pipelines and code repositories")
                .status(PhishingCampaign.CampaignStatus.ACTIVE)
                .threatLevel(PhishingCampaign.ThreatLevel.CRITICAL)
                .targetSector("Technology")
                .discoveredAt(LocalDateTime.now().minusDays(6))
                .estimatedVictims(156)
                .geographicScope("Global")
                .attackVectors(Arrays.asList("Code Injection", "Package Poisoning", "CI/CD Compromise"))
                .build());

        campaigns = campaignRepository.saveAll(campaigns);
        log.info("Generated {} realistic phishing campaigns", campaigns.size());
        return campaigns;
    }

    private void generateInfrastructureNetworks(List<PhishingCampaign> campaigns) {
        log.info("Generating infrastructure networks...");

        for (PhishingCampaign campaign : campaigns) {
            List<InfrastructureNode> nodes = generateNodesForCampaign(campaign);
            nodeRepository.saveAll(nodes);
            
            // Create realistic connections between nodes
            createNodeConnections(nodes);
        }

        log.info("Generated infrastructure networks for all campaigns");
    }

    private List<InfrastructureNode> generateNodesForCampaign(PhishingCampaign campaign) {
        List<InfrastructureNode> nodes = new ArrayList<>();
        
        // Generate different types of nodes based on campaign characteristics
        int nodeCount = ThreadLocalRandom.current().nextInt(8, 25);
        
        for (int i = 0; i < nodeCount; i++) {
            InfrastructureNode.NodeType nodeType = getRandomNodeType();
            String identifier = generateRealisticIdentifier(nodeType, campaign.getName());
            
            nodes.add(InfrastructureNode.builder()
                    .campaign(campaign)
                    .identifier(identifier)
                    .nodeType(nodeType)
                    .description(generateNodeDescription(nodeType, identifier))
                    .status(getRandomNodeStatus())
                    .riskScore(ThreadLocalRandom.current().nextDouble(0.1, 1.0))
                    .technicalDetails(generateTechnicalDetails(nodeType))
                    .discoveredAt(campaign.getDiscoveredAt().plusDays(ThreadLocalRandom.current().nextInt(0, 10)))
                    .lastCheckedAt(LocalDateTime.now().minusHours(ThreadLocalRandom.current().nextInt(1, 48)))
                    .build());
        }
        
        return nodes;
    }

    private InfrastructureNode.NodeType getRandomNodeType() {
        InfrastructureNode.NodeType[] types = InfrastructureNode.NodeType.values();
        return types[random.nextInt(types.length)];
    }

    private String generateRealisticIdentifier(InfrastructureNode.NodeType nodeType, String campaignName) {
        switch (nodeType) {
            case DOMAIN:
                return generateRealisticDomain(campaignName);
            case IP_ADDRESS:
                return generateRealisticIP();
            case URL:
                return generateRealisticURL(campaignName);
            case EMAIL_SERVER:
                return "mail." + generateRealisticDomain(campaignName);
            default:
                return generateRealisticDomain(campaignName);
        }
    }

    private String generateRealisticDomain(String campaignName) {
        String[] prefixes = {"secure", "login", "auth", "verify", "account", "update", "service", "portal"};
        String[] suffixes = {"bank", "pay", "cloud", "tech", "corp", "net", "secure", "online"};
        String[] tlds = {".com", ".net", ".org", ".info", ".biz", ".co"};
        
        String prefix = prefixes[random.nextInt(prefixes.length)];
        String suffix = suffixes[random.nextInt(suffixes.length)];
        String tld = tlds[random.nextInt(tlds.length)];
        
        return prefix + "-" + suffix + random.nextInt(1000) + tld;
    }

    private String generateRealisticIP() {
        return String.format("%d.%d.%d.%d",
                ThreadLocalRandom.current().nextInt(1, 255),
                ThreadLocalRandom.current().nextInt(0, 255),
                ThreadLocalRandom.current().nextInt(0, 255),
                ThreadLocalRandom.current().nextInt(1, 255));
    }

    private String generateRealisticURL(String campaignName) {
        return "https://" + generateRealisticDomain(campaignName) + "/login";
    }

    // Additional helper methods continue...
}
