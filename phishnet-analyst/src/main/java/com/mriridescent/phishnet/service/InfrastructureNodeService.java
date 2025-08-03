package com.mriridescent.phishnet.service;

import com.mriridescent.phishnet.model.dto.InfrastructureNodeDTO;

import java.util.List;

/**
 * Service interface for infrastructure node operations.
 */
public interface InfrastructureNodeService {

    /**
     * Creates a new infrastructure node.
     *
     * @param campaignId The campaign ID.
     * @param nodeDto    The node data.
     * @return The created node.
     */
    InfrastructureNodeDTO createNode(Long campaignId, InfrastructureNodeDTO nodeDto);

    /**
     * Retrieves a node by ID.
     *
     * @param id The node ID.
     * @return The node.
     */
    InfrastructureNodeDTO getNodeById(Long id);

    /**
     * Retrieves all nodes for a campaign.
     *
     * @param campaignId The campaign ID.
     * @return A list of all nodes for the campaign.
     */
    List<InfrastructureNodeDTO> getNodesForCampaign(Long campaignId);

    /**
     * Updates a node's status.
     *
     * @param id     The node ID.
     * @param status The new status.
     * @return The updated node.
     */
    InfrastructureNodeDTO updateNodeStatus(Long id, String status);

    /**
     * Connects two nodes.
     *
     * @param sourceId The source node ID.
     * @param targetId The target node ID.
     * @return The updated source node.
     */
    InfrastructureNodeDTO connectNodes(Long sourceId, Long targetId);

    /**
     * Disconnects two nodes.
     *
     * @param sourceId The source node ID.
     * @param targetId The target node ID.
     * @return The updated source node.
     */
    InfrastructureNodeDTO disconnectNodes(Long sourceId, Long targetId);
}
