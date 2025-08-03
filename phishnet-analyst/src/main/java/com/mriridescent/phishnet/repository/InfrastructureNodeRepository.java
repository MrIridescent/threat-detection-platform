package com.mriridescent.phishnet.repository;

import com.mriridescent.phishnet.model.entity.InfrastructureNode;
import com.mriridescent.phishnet.model.entity.PhishingCampaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for InfrastructureNode entities.
 */
@Repository
public interface InfrastructureNodeRepository extends JpaRepository<InfrastructureNode, Long> {

    /**
     * Finds nodes by campaign.
     *
     * @param campaign The campaign to filter by.
     * @return A list of nodes for the specified campaign.
     */
    List<InfrastructureNode> findByCampaign(PhishingCampaign campaign);

    /**
     * Finds nodes by node type.
     *
     * @param nodeType The node type to filter by.
     * @return A list of nodes with the specified type.
     */
    List<InfrastructureNode> findByNodeType(InfrastructureNode.NodeType nodeType);

    /**
     * Finds nodes by status.
     *
     * @param status The status to filter by.
     * @return A list of nodes with the specified status.
     */
    List<InfrastructureNode> findByStatus(InfrastructureNode.NodeStatus status);

    /**
     * Finds nodes by identifier.
     *
     * @param identifier The identifier to filter by.
     * @return A list of nodes with the specified identifier.
     */
    List<InfrastructureNode> findByIdentifierContainingIgnoreCase(String identifier);

    /**
     * Finds nodes by malicious flag.
     *
     * @param isMalicious The malicious flag to filter by.
     * @return A list of nodes with the specified malicious flag.
     */
    List<InfrastructureNode> findByIsMalicious(boolean isMalicious);

    /**
     * Finds nodes by campaign and node type.
     *
     * @param campaign The campaign to filter by.
     * @param nodeType The node type to filter by.
     * @return A list of nodes for the specified campaign and type.
     */
    List<InfrastructureNode> findByCampaignAndNodeType(PhishingCampaign campaign, InfrastructureNode.NodeType nodeType);
}
