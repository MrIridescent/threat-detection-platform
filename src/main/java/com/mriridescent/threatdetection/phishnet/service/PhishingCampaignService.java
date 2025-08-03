package com.mriridescent.threatdetection.phishnet.service;
package com.mriridescent.threatdetection.phishnet.service;

import com.mriridescent.threatdetection.model.User;
import com.mriridescent.threatdetection.phishnet.model.entity.PhishingCampaign;
import com.mriridescent.threatdetection.phishnet.repository.PhishingCampaignRepository;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing phishing campaigns.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PhishingCampaignService {

    private final PhishingCampaignRepository campaignRepository;

    /**
     * Get all phishing campaigns
     *
     * @return List of all campaigns
     */
    @Cacheable(value = "phishingCampaigns")
    @Timed(value = "phishnet.campaign.fetch.all", description = "Time taken to fetch all phishing campaigns")
    public List<PhishingCampaign> getAllCampaigns() {
        log.info("Fetching all phishing campaigns");
        return campaignRepository.findAll();
    }

    /**
     * Get all phishing campaigns (paginated)
     *
     * @param pageable Pagination information
     * @return Page of campaigns
     */
    @Timed(value = "phishnet.campaign.fetch.paged", description = "Time taken to fetch paged phishing campaigns")
    public Page<PhishingCampaign> getAllCampaigns(Pageable pageable) {
        log.info("Fetching paged phishing campaigns");
        return campaignRepository.findAll(pageable);
    }

    /**
     * Get active phishing campaigns
     *
     * @return List of active campaigns
     */
    @Cacheable(value = "activeCampaigns")
    @Timed(value = "phishnet.campaign.fetch.active", description = "Time taken to fetch active phishing campaigns")
    public List<PhishingCampaign> getActiveCampaigns() {
        log.info("Fetching active phishing campaigns");
        return campaignRepository.findByStatusIn(
                List.of(PhishingCampaign.CampaignStatus.ACTIVE, PhishingCampaign.CampaignStatus.ESCALATING));
    }

    /**
     * Get phishing campaign by ID
     *
     * @param id Campaign ID
     * @return Optional containing the campaign if found
     */
    @Cacheable(value = "phishingCampaign", key = "#id")
    public Optional<PhishingCampaign> getCampaignById(Long id) {
        log.info("Fetching phishing campaign with ID: {}", id);
        return campaignRepository.findById(id);
    }

    /**
     * Create a new phishing campaign
     *
     * @param campaign The campaign to create
     * @return The created campaign
     */
    @Transactional
    @CacheEvict(value = {"phishingCampaigns", "activeCampaigns"}, allEntries = true)
    @Timed(value = "phishnet.campaign.create", description = "Time taken to create a new phishing campaign")
    public PhishingCampaign createCampaign(PhishingCampaign campaign) {
        log.info("Creating new phishing campaign: {}", campaign.getName());

        // Set current user as creator
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            campaign.setCreatedBy((User) authentication.getPrincipal());
        }

        campaign.setCreatedAt(LocalDateTime.now());
        campaign.setLastUpdatedAt(LocalDateTime.now());

        return campaignRepository.save(campaign);
    }

    /**
     * Update an existing phishing campaign
     *
     * @param id Campaign ID
     * @param updatedCampaign Updated campaign data
     * @return The updated campaign
     */
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "phishingCampaign", key = "#id"),
        @CacheEvict(value = {"phishingCampaigns", "activeCampaigns"}, allEntries = true)
    })
    @Timed(value = "phishnet.campaign.update", description = "Time taken to update a phishing campaign")
    public PhishingCampaign updateCampaign(Long id, PhishingCampaign updatedCampaign) {
        log.info("Updating phishing campaign with ID: {}", id);

        return campaignRepository.findById(id)
                .map(existingCampaign -> {
                    existingCampaign.setName(updatedCampaign.getName());
                    existingCampaign.setDescription(updatedCampaign.getDescription());
                    existingCampaign.setStatus(updatedCampaign.getStatus());
                    existingCampaign.setSeverity(updatedCampaign.getSeverity());
                    existingCampaign.setTargetDemographic(updatedCampaign.getTargetDemographic());
                    existingCampaign.setEstimatedReach(updatedCampaign.getEstimatedReach());
                    existingCampaign.setOriginRegion(updatedCampaign.getOriginRegion());
                    existingCampaign.setTechnicalDetails(updatedCampaign.getTechnicalDetails());
                    existingCampaign.setLastUpdatedAt(LocalDateTime.now());

                    // Don't overwrite first observed date if it's already set
                    if (existingCampaign.getFirstObservedAt() == null && updatedCampaign.getFirstObservedAt() != null) {
                        existingCampaign.setFirstObservedAt(updatedCampaign.getFirstObservedAt());
                    }

                    existingCampaign.setLastObservedAt(updatedCampaign.getLastObservedAt());

                    return campaignRepository.save(existingCampaign);
                })
                .orElseThrow(() -> new RuntimeException("Campaign not found with id: " + id));
    }

    /**
     * Delete a phishing campaign
     *
     * @param id Campaign ID
     */
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "phishingCampaign", key = "#id"),
        @CacheEvict(value = {"phishingCampaigns", "activeCampaigns"}, allEntries = true)
    })
    public void deleteCampaign(Long id) {
        log.info("Deleting phishing campaign with ID: {}", id);
        campaignRepository.deleteById(id);
    }

    /**
     * Update campaign status
     *
     * @param id Campaign ID
     * @param status New status
     * @return The updated campaign
     */
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "phishingCampaign", key = "#id"),
        @CacheEvict(value = {"phishingCampaigns", "activeCampaigns"}, allEntries = true)
    })
    public PhishingCampaign updateCampaignStatus(Long id, PhishingCampaign.CampaignStatus status) {
        log.info("Updating status of phishing campaign with ID: {} to {}", id, status);

        return campaignRepository.findById(id)
                .map(campaign -> {
                    campaign.setStatus(status);
                    campaign.setLastUpdatedAt(LocalDateTime.now());
                    return campaignRepository.save(campaign);
                })
                .orElseThrow(() -> new RuntimeException("Campaign not found with id: " + id));
    }

    /**
     * Update campaign severity
     *
     * @param id Campaign ID
     * @param severity New severity
     * @return The updated campaign
     */
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "phishingCampaign", key = "#id"),
        @CacheEvict(value = {"phishingCampaigns", "activeCampaigns"}, allEntries = true)
    })
    public PhishingCampaign updateCampaignSeverity(Long id, PhishingCampaign.Severity severity) {
        log.info("Updating severity of phishing campaign with ID: {} to {}", id, severity);

        return campaignRepository.findById(id)
                .map(campaign -> {
                    campaign.setSeverity(severity);
                    campaign.setLastUpdatedAt(LocalDateTime.now());
                    return campaignRepository.save(campaign);
                })
                .orElseThrow(() -> new RuntimeException("Campaign not found with id: " + id));
    }

    /**
     * Search campaigns by name or description
     *
     * @param query Search query
     * @return List of matching campaigns
     */
    public List<PhishingCampaign> searchCampaigns(String query) {
        log.info("Searching phishing campaigns with query: {}", query);
        return campaignRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
    }

    /**
     * Get campaigns by severity level
     *
     * @param severity Severity level
     * @return List of campaigns with the specified severity
     */
    public List<PhishingCampaign> getCampaignsBySeverity(PhishingCampaign.Severity severity) {
        log.info("Fetching phishing campaigns with severity: {}", severity);
        return campaignRepository.findBySeverity(severity);
    }

    /**
     * Get campaigns by status
     *
     * @param status Campaign status
     * @return List of campaigns with the specified status
     */
    public List<PhishingCampaign> getCampaignsByStatus(PhishingCampaign.CampaignStatus status) {
        log.info("Fetching phishing campaigns with status: {}", status);
        return campaignRepository.findByStatus(status);
    }
}
import com.mriridescent.threatdetection.iris.model.entity.EmailAnalysis;
import com.mriridescent.threatdetection.model.entity.User;
import com.mriridescent.threatdetection.phishnet.model.entity.AttackVector;
import com.mriridescent.threatdetection.phishnet.model.entity.InfrastructureNode;
import com.mriridescent.threatdetection.phishnet.model.entity.PhishingCampaign;
import com.mriridescent.threatdetection.phishnet.repository.PhishingCampaignRepository;
import com.mriridescent.threatdetection.phishnet.repository.InfrastructureNodeRepository;
import com.mriridescent.threatdetection.phishnet.repository.AttackVectorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service for managing phishing campaigns in PhishNet Analyst.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PhishingCampaignService {

    private final PhishingCampaignRepository campaignRepository;
    private final InfrastructureNodeRepository nodeRepository;
    private final AttackVectorRepository vectorRepository;

    private static final Pattern URL_PATTERN = 
            Pattern.compile("https?://[\\w\\.-]+\.\\w+(/[\\w\\.-]*)*");

    /**
     * Creates a new phishing campaign.
     *
     * @param name               Campaign name
     * @param description        Campaign description
     * @param threatLevel        Threat level
     * @param targetSector       Target sector
     * @param tacticsAndTechniques Tactics and techniques used
     * @param creator            User creating the campaign
     * @return The created campaign
     */
    @Transactional
    public PhishingCampaign createCampaign(String name, String description, 
                                          PhishingCampaign.ThreatLevel threatLevel,
                                          String targetSector, String tacticsAndTechniques,
                                          User creator) {
        log.info("Creating new phishing campaign: {}", name);

        PhishingCampaign campaign = PhishingCampaign.builder()
                .name(name)
                .description(description)
                .status(PhishingCampaign.CampaignStatus.ACTIVE)
                .threatLevel(threatLevel)
                .targetSector(targetSector)
                .discoveredAt(LocalDateTime.now())
                .tacticsAndTechniques(tacticsAndTechniques)
                .createdBy(creator)
                .build();

        return campaignRepository.save(campaign);
    }

    /**
     * Adds an infrastructure node to a campaign.
     *
     * @param campaignId  Campaign ID
     * @param identifier  Node identifier (IP, domain, etc.)
     * @param nodeType    Type of node
     * @param description Node description
     * @param isMalicious Whether the node is malicious
     * @return The created node
     */
    @Transactional
    public InfrastructureNode addInfrastructureNode(Long campaignId, String identifier,
                                                  InfrastructureNode.NodeType nodeType,
                                                  String description, boolean isMalicious) {
        PhishingCampaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new IllegalArgumentException("Campaign not found"));

        InfrastructureNode node = InfrastructureNode.builder()
                .campaign(campaign)
                .identifier(identifier)
                .nodeType(nodeType)
                .description(description)
                .status(InfrastructureNode.NodeStatus.ACTIVE)
                .isMalicious(isMalicious)
                .build();

        return nodeRepository.save(node);
    }

    /**
     * Adds an attack vector to a campaign.
     *
     * @param campaignId        Campaign ID
     * @param name              Vector name
     * @param vectorType        Type of attack vector
     * @param description       Vector description
     * @param technicalDetails  Technical details
     * @param prevalence        Prevalence score (1-10)
     * @param effectiveness     Effectiveness score (1-10)
     * @return The created attack vector
     */
    @Transactional
    public AttackVector addAttackVector(Long campaignId, String name,
                                      AttackVector.VectorType vectorType,
                                      String description, String technicalDetails,
                                      int prevalence, int effectiveness) {
        PhishingCampaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new IllegalArgumentException("Campaign not found"));

        AttackVector vector = AttackVector.builder()
                .campaign(campaign)
                .name(name)
                .vectorType(vectorType)
                .description(description)
                .status(AttackVector.VectorStatus.ACTIVE)
                .technicalDetails(technicalDetails)
                .prevalence(prevalence)
                .effectiveness(effectiveness)
                .build();

        return vectorRepository.save(vector);
    }

    /**
     * Updates the status of a campaign.
     *
     * @param campaignId Campaign ID
     * @param status     New status
     * @return The updated campaign
     */
    @Transactional
    public PhishingCampaign updateCampaignStatus(Long campaignId, PhishingCampaign.CampaignStatus status) {
        PhishingCampaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new IllegalArgumentException("Campaign not found"));

        campaign.setStatus(status);

        if (status == PhishingCampaign.CampaignStatus.RESOLVED || 
            status == PhishingCampaign.CampaignStatus.ARCHIVED) {
            campaign.setResolvedAt(LocalDateTime.now());
        }

        return campaignRepository.save(campaign);
    }

    /**
     * Gets a list of all active campaigns.
     *
     * @return List of active campaigns
     */
    public List<PhishingCampaign> getActiveCampaigns() {
        return campaignRepository.findByStatusIn(
                List.of(PhishingCampaign.CampaignStatus.ACTIVE, PhishingCampaign.CampaignStatus.MONITORING));
    }

    /**
     * Gets a paged list of all campaigns.
     *
     * @param pageable Pagination information
     * @return Page of campaigns
     */
    public Page<PhishingCampaign> getAllCampaigns(Pageable pageable) {
        return campaignRepository.findAll(pageable);
    }

    /**
     * Gets detailed information about a campaign.
     *
     * @param campaignId Campaign ID
     * @return The campaign with all related information
     */
    public PhishingCampaign getCampaignDetails(Long campaignId) {
        return campaignRepository.findByIdWithNodesAndVectors(campaignId)
                .orElseThrow(() -> new IllegalArgumentException("Campaign not found"));
    }

    /**
     * Sets a mitigation strategy for a campaign.
     *
     * @param campaignId         Campaign ID
     * @param mitigationStrategy The mitigation strategy
     * @return The updated campaign
     */
    @Transactional
    public PhishingCampaign setMitigationStrategy(Long campaignId, String mitigationStrategy) {
        PhishingCampaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new IllegalArgumentException("Campaign not found"));

        campaign.setMitigationStrategy(mitigationStrategy);
        campaign.setStatus(PhishingCampaign.CampaignStatus.MONITORING);

        return campaignRepository.save(campaign);
    }

    /**
     * Connects an infrastructure node to others in the network.
     *
     * @param nodeId         ID of the node to connect from
     * @param targetNodeIds  IDs of the nodes to connect to
     * @return The updated node
     */
    @Transactional
    public InfrastructureNode connectNodes(Long nodeId, Set<Long> targetNodeIds) {
        InfrastructureNode sourceNode = nodeRepository.findById(nodeId)
                .orElseThrow(() -> new IllegalArgumentException("Source node not found"));

        List<InfrastructureNode> targetNodes = nodeRepository.findAllById(targetNodeIds);

        sourceNode.getConnectedTo().addAll(targetNodes);

        return nodeRepository.save(sourceNode);
    }

    /**
     * Checks if an email analysis is related to an existing campaign.
     *
     * @param analysis The email analysis to check
     */
    @Transactional
    public void checkForCampaignAssociation(EmailAnalysis analysis) {
        // Extract potential campaign indicators
        String sender = analysis.getSender();
        String content = analysis.getContent();
        String senderDomain = extractDomain(sender);
        List<String> urls = extractUrls(content);

        // Check for domain matches in existing campaigns
        List<InfrastructureNode> matchingDomainNodes = nodeRepository.findByIdentifierContaining(senderDomain);

        if (!matchingDomainNodes.isEmpty()) {
            // Found matching domain in infrastructure nodes
            PhishingCampaign campaign = matchingDomainNodes.get(0).getCampaign();
            log.info("Email from {} matched with existing campaign: {}", sender, campaign.getName());
            return;
        }

        // Check for URL matches in existing campaigns
        for (String url : urls) {
            try {
                URI uri = new URI(url);
                String host = uri.getHost();
                Optional<InfrastructureNode> matchingNode = nodeRepository.findByIdentifier(host);

                if (matchingNode.isPresent()) {
                    // Found matching URL in infrastructure nodes
                    PhishingCampaign campaign = matchingNode.get().getCampaign();
                    log.info("URL {} in email matched with existing campaign: {}", url, campaign.getName());
                    return;
                }
            } catch (URISyntaxException e) {
                log.warn("Invalid URL in email: {}", url);
            }
        }

        // If threat level is critical, consider creating a new campaign
        if (analysis.getThreatLevel() == EmailAnalysis.ThreatLevel.CRITICAL) {
            log.info("Critical threat detected, consider creating a new campaign for email from {}", sender);
            // In a full implementation, this could automatically create a campaign
            // or send a notification to analysts
        }
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
}
