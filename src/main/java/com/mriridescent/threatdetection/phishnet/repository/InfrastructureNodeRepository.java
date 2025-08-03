package com.mriridescent.threatdetection.phishnet.repository;

import com.mriridescent.threatdetection.phishnet.model.entity.InfrastructureNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for InfrastructureNode entities.
 */
@Repository
public interface InfrastructureNodeRepository extends JpaRepository<InfrastructureNode, Long> {

    /**
     * Finds nodes by their identifier.
     *
     * @param identifier Identifier to search for
     * @return Optional containing the node if found
     */
    Optional<InfrastructureNode> findByIdentifier(String identifier);

    /**
     * Finds nodes with identifiers containing the given string.
     *
     * @param identifier Part of identifier to search for
     * @return List of matching nodes
     */
    List<InfrastructureNode> findByIdentifierContaining(String identifier);

    /**
     * Finds nodes by node type.
     *
     * @param nodeType Node type to search for
     * @return List of matching nodes
     */
    List<InfrastructureNode> findByNodeType(InfrastructureNode.NodeType nodeType);

    /**
     * Finds nodes for a specific campaign.
     *
     * @param campaignId Campaign ID to search for
     * @return List of matching nodes
     */
    List<InfrastructureNode> findByCampaignId(Long campaignId);

    /**
     * Finds malicious nodes.
     *
     * @return List of malicious nodes
     */
    List<InfrastructureNode> findByIsMaliciousTrue();
}
