package com.mriridescent.threatdetection.phishnet.mapper;

import com.mriridescent.threatdetection.phishnet.dto.*;
import com.mriridescent.threatdetection.phishnet.model.entity.AttackVector;
import com.mriridescent.threatdetection.phishnet.model.entity.InfrastructureNode;
import com.mriridescent.threatdetection.phishnet.model.entity.PhishingCampaign;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between PhishingCampaign entities and DTOs.
 */
@Mapper(componentModel = "spring")
public interface PhishingCampaignMapper {

    @Mapping(target = "createdByUsername", source = "createdBy.username")
    @Mapping(target = "assignedToUsername", source = "assignedTo.username")
    @Mapping(target = "nodeCount", expression = "java(campaign.getInfrastructureNodes().size())")
    @Mapping(target = "vectorCount", expression = "java(campaign.getAttackVectors().size())")
    CampaignDTO toDto(PhishingCampaign campaign);

    @Mapping(target = "createdByUsername", source = "createdBy.username")
    @Mapping(target = "assignedToUsername", source = "assignedTo.username")
    @Mapping(target = "infrastructureNodes", expression = "java(toNodeDtoList(campaign.getInfrastructureNodes()))")
    @Mapping(target = "attackVectors", expression = "java(toVectorDtoList(campaign.getAttackVectors()))")
    CampaignDetailDTO toDetailDto(PhishingCampaign campaign);

    @Mapping(target = "campaignId", source = "campaign.id")
    @Mapping(target = "connectedToIds", expression = "java(node.getConnectedTo().stream().map(n -> n.getId()).collect(Collectors.toList()))")
    InfrastructureNodeDTO toNodeDto(InfrastructureNode node);

    @Mapping(target = "campaignId", source = "campaign.id")
    AttackVectorDTO toVectorDto(AttackVector vector);

    List<CampaignDTO> toDtoList(List<PhishingCampaign> campaigns);

    default List<InfrastructureNodeDTO> toNodeDtoList(java.util.Set<InfrastructureNode> nodes) {
        if (nodes == null) {
            return java.util.Collections.emptyList();
        }
        return nodes.stream()
                .map(this::toNodeDto)
                .collect(Collectors.toList());
    }

    default List<AttackVectorDTO> toVectorDtoList(java.util.Set<AttackVector> vectors) {
        if (vectors == null) {
            return java.util.Collections.emptyList();
        }
        return vectors.stream()
                .map(this::toVectorDto)
                .collect(Collectors.toList());
    }
}
