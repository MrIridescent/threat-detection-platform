package com.mriridescent.threatdetection.phishnet.controller;
package com.mriridescent.threatdetection.phishnet.controller;

import com.mriridescent.threatdetection.controller.ApiResponse;
import com.mriridescent.threatdetection.phishnet.dto.PhishingCampaignDto;
import com.mriridescent.threatdetection.phishnet.model.entity.PhishingCampaign;
import com.mriridescent.threatdetection.phishnet.service.PhishingCampaignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for phishing campaign management operations.
 */
@RestController
@RequestMapping("/api/v1/phishnet/campaigns")
@RequiredArgsConstructor
@Tag(name = "Phishing Campaigns", description = "Phishing campaign management operations")
public class PhishingCampaignController {

    private final PhishingCampaignService campaignService;

    /**
     * Get all phishing campaigns
     *
     * @return List of all campaigns
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    @Operation(summary = "Get all phishing campaigns", description = "Retrieves all phishing campaigns")
    public ResponseEntity<ApiResponse<List<PhishingCampaignDto>>> getAllCampaigns() {
        List<PhishingCampaignDto> campaigns = campaignService.getAllCampaigns().stream()
                .map(PhishingCampaignDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("Campaigns retrieved successfully", campaigns));
    }

    /**
     * Get phishing campaigns (paginated)
     *
     * @param page Page number
     * @param size Page size
     * @param sort Sort field
     * @param direction Sort direction
     * @return Page of campaigns
     */
    @GetMapping("/paged")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    @Operation(summary = "Get paged phishing campaigns", description = "Retrieves phishing campaigns with pagination")
    public ResponseEntity<ApiResponse<Page<PhishingCampaignDto>>> getPagedCampaigns(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
        PageRequest pageRequest = PageRequest.of(page, size, sortDirection, sort);

        Page<PhishingCampaignDto> campaigns = campaignService.getAllCampaigns(pageRequest)
                .map(PhishingCampaignDto::fromEntity);

        return ResponseEntity.ok(ApiResponse.success("Campaigns retrieved successfully", campaigns));
    }

    /**
     * Get active phishing campaigns
     *
     * @return List of active campaigns
     */
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    @Operation(summary = "Get active phishing campaigns", description = "Retrieves all active phishing campaigns")
    public ResponseEntity<ApiResponse<List<PhishingCampaignDto>>> getActiveCampaigns() {
        List<PhishingCampaignDto> campaigns = campaignService.getActiveCampaigns().stream()
                .map(PhishingCampaignDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("Active campaigns retrieved successfully", campaigns));
    }

    /**
     * Get phishing campaign by ID
     *
     * @param id Campaign ID
     * @return Campaign details
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    @Operation(summary = "Get phishing campaign by ID", description = "Retrieves a specific phishing campaign by its ID")
    public ResponseEntity<ApiResponse<PhishingCampaignDto>> getCampaignById(@PathVariable Long id) {
        return campaignService.getCampaignById(id)
                .map(campaign -> ResponseEntity.ok(ApiResponse.success(
                        "Campaign retrieved successfully", 
                        PhishingCampaignDto.fromEntity(campaign))))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Campaign not found with id: " + id)));
    }

    /**
     * Create a new phishing campaign
     *
     * @param campaignDto Campaign details
     * @return Created campaign
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    @Operation(summary = "Create a new phishing campaign", description = "Creates a new phishing campaign")
    public ResponseEntity<ApiResponse<PhishingCampaignDto>> createCampaign(@Valid @RequestBody PhishingCampaignDto campaignDto) {
        PhishingCampaign savedCampaign = campaignService.createCampaign(campaignDto.toEntity());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Campaign created successfully", PhishingCampaignDto.fromEntity(savedCampaign)));
    }

    /**
     * Update an existing phishing campaign
     *
     * @param id Campaign ID
     * @param campaignDto Updated campaign details
     * @return Updated campaign
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    @Operation(summary = "Update a phishing campaign", description = "Updates an existing phishing campaign")
    public ResponseEntity<ApiResponse<PhishingCampaignDto>> updateCampaign(
            @PathVariable Long id, 
            @Valid @RequestBody PhishingCampaignDto campaignDto) {
        PhishingCampaign updatedCampaign = campaignService.updateCampaign(id, campaignDto.toEntity());
        return ResponseEntity.ok(ApiResponse.success("Campaign updated successfully", PhishingCampaignDto.fromEntity(updatedCampaign)));
    }

    /**
     * Delete a phishing campaign
     *
     * @param id Campaign ID
     * @return Success response
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a phishing campaign", description = "Deletes an existing phishing campaign")
    public ResponseEntity<ApiResponse<Void>> deleteCampaign(@PathVariable Long id) {
        campaignService.deleteCampaign(id);
        return ResponseEntity.ok(ApiResponse.success("Campaign deleted successfully"));
    }

    /**
     * Update campaign status
     *
     * @param id Campaign ID
     * @param status New status
     * @return Updated campaign
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    @Operation(summary = "Update campaign status", description = "Updates the status of a phishing campaign")
    public ResponseEntity<ApiResponse<PhishingCampaignDto>> updateCampaignStatus(
            @PathVariable Long id, 
            @RequestParam PhishingCampaign.CampaignStatus status) {
        PhishingCampaign updatedCampaign = campaignService.updateCampaignStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Campaign status updated successfully", PhishingCampaignDto.fromEntity(updatedCampaign)));
    }

    /**
     * Update campaign severity
     *
     * @param id Campaign ID
     * @param severity New severity
     * @return Updated campaign
     */
    @PatchMapping("/{id}/severity")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    @Operation(summary = "Update campaign severity", description = "Updates the severity of a phishing campaign")
    public ResponseEntity<ApiResponse<PhishingCampaignDto>> updateCampaignSeverity(
            @PathVariable Long id, 
            @RequestParam PhishingCampaign.Severity severity) {
        PhishingCampaign updatedCampaign = campaignService.updateCampaignSeverity(id, severity);
        return ResponseEntity.ok(ApiResponse.success("Campaign severity updated successfully", PhishingCampaignDto.fromEntity(updatedCampaign)));
    }

    /**
     * Search campaigns by name or description
     *
     * @param query Search query
     * @return List of matching campaigns
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    @Operation(summary = "Search phishing campaigns", description = "Searches for phishing campaigns by name or description")
    public ResponseEntity<ApiResponse<List<PhishingCampaignDto>>> searchCampaigns(@RequestParam String query) {
        List<PhishingCampaignDto> campaigns = campaignService.searchCampaigns(query).stream()
                .map(PhishingCampaignDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("Search results retrieved successfully", campaigns));
    }

    /**
     * Get campaigns by severity
     *
     * @param severity Severity level
     * @return List of campaigns with the specified severity
     */
    @GetMapping("/by-severity/{severity}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    @Operation(summary = "Get campaigns by severity", description = "Retrieves phishing campaigns with a specific severity level")
    public ResponseEntity<ApiResponse<List<PhishingCampaignDto>>> getCampaignsBySeverity(
            @PathVariable PhishingCampaign.Severity severity) {
        List<PhishingCampaignDto> campaigns = campaignService.getCampaignsBySeverity(severity).stream()
                .map(PhishingCampaignDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("Campaigns retrieved successfully", campaigns));
    }

    /**
     * Get campaigns by status
     *
     * @param status Campaign status
     * @return List of campaigns with the specified status
     */
    @GetMapping("/by-status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    @Operation(summary = "Get campaigns by status", description = "Retrieves phishing campaigns with a specific status")
    public ResponseEntity<ApiResponse<List<PhishingCampaignDto>>> getCampaignsByStatus(
            @PathVariable PhishingCampaign.CampaignStatus status) {
        List<PhishingCampaignDto> campaigns = campaignService.getCampaignsByStatus(status).stream()
                .map(PhishingCampaignDto::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("Campaigns retrieved successfully", campaigns));
    }
}
import com.mriridescent.threatdetection.model.entity.User;
import com.mriridescent.threatdetection.phishnet.dto.*;
import com.mriridescent.threatdetection.phishnet.mapper.PhishingCampaignMapper;
import com.mriridescent.threatdetection.phishnet.model.entity.AttackVector;
import com.mriridescent.threatdetection.phishnet.model.entity.InfrastructureNode;
import com.mriridescent.threatdetection.phishnet.model.entity.PhishingCampaign;
import com.mriridescent.threatdetection.phishnet.service.PhishingCampaignService;
import com.mriridescent.threatdetection.security.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for phishing campaign operations in PhishNet Analyst.
 */
@RestController
@RequestMapping("/api/v1/phishnet")
@RequiredArgsConstructor
@Tag(name = "Phishing Campaigns", description = "API for managing phishing campaign analysis")
public class PhishingCampaignController {

    private final PhishingCampaignService campaignService;
    private final PhishingCampaignMapper campaignMapper;

    @PostMapping("/campaigns")
    @Operation(summary = "Create campaign", 
              description = "Creates a new phishing campaign")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    public ResponseEntity<CampaignDTO> createCampaign(
            @Valid @RequestBody CampaignCreationDTO creationDTO,
            @AuthenticationPrincipal User currentUser) {

        PhishingCampaign campaign = campaignService.createCampaign(
                creationDTO.getName(),
                creationDTO.getDescription(),
                creationDTO.getThreatLevel(),
                creationDTO.getTargetSector(),
                creationDTO.getTacticsAndTechniques(),
                currentUser
        );

        return ResponseEntity.ok(campaignMapper.toDto(campaign));
    }

    @GetMapping("/campaigns")
    @Operation(summary = "Get campaigns", 
              description = "Retrieves a paginated list of all campaigns")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    public ResponseEntity<Page<CampaignDTO>> getCampaigns(Pageable pageable) {
        Page<PhishingCampaign> campaigns = campaignService.getAllCampaigns(pageable);
        return ResponseEntity.ok(campaigns.map(campaignMapper::toDto));
    }

    @GetMapping("/campaigns/active")
    @Operation(summary = "Get active campaigns", 
              description = "Retrieves a list of all active campaigns")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    public ResponseEntity<List<CampaignDTO>> getActiveCampaigns() {
        List<PhishingCampaign> campaigns = campaignService.getActiveCampaigns();
        return ResponseEntity.ok(campaignMapper.toDtoList(campaigns));
    }

    @GetMapping("/campaigns/{id}")
    @Operation(summary = "Get campaign details", 
              description = "Retrieves detailed information about a specific campaign")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    public ResponseEntity<CampaignDetailDTO> getCampaignDetails(@PathVariable Long id) {
        PhishingCampaign campaign = campaignService.getCampaignDetails(id);
        return ResponseEntity.ok(campaignMapper.toDetailDto(campaign));
    }

    @PutMapping("/campaigns/{id}/status")
    @Operation(summary = "Update campaign status", 
              description = "Updates the status of a campaign")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    public ResponseEntity<CampaignDTO> updateCampaignStatus(
            @PathVariable Long id, 
            @Valid @RequestBody StatusUpdateDTO statusUpdate) {

        PhishingCampaign campaign = campaignService.updateCampaignStatus(id, statusUpdate.getStatus());
        return ResponseEntity.ok(campaignMapper.toDto(campaign));
    }

    @PutMapping("/campaigns/{id}/mitigation")
    @Operation(summary = "Set mitigation strategy", 
              description = "Sets a mitigation strategy for a campaign")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    public ResponseEntity<CampaignDTO> setMitigationStrategy(
            @PathVariable Long id, 
            @Valid @RequestBody MitigationDTO mitigationDTO) {

        PhishingCampaign campaign = campaignService.setMitigationStrategy(id, mitigationDTO.getStrategy());
        return ResponseEntity.ok(campaignMapper.toDto(campaign));
    }

    @PostMapping("/campaigns/{id}/nodes")
    @Operation(summary = "Add infrastructure node", 
              description = "Adds an infrastructure node to a campaign")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    public ResponseEntity<InfrastructureNodeDTO> addInfrastructureNode(
            @PathVariable Long id, 
            @Valid @RequestBody NodeCreationDTO nodeDTO) {

        InfrastructureNode node = campaignService.addInfrastructureNode(
                id,
                nodeDTO.getIdentifier(),
                nodeDTO.getNodeType(),
                nodeDTO.getDescription(),
                nodeDTO.isMalicious()
        );

        return ResponseEntity.ok(campaignMapper.toNodeDto(node));
    }

    @PostMapping("/campaigns/{id}/vectors")
    @Operation(summary = "Add attack vector", 
              description = "Adds an attack vector to a campaign")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    public ResponseEntity<AttackVectorDTO> addAttackVector(
            @PathVariable Long id, 
            @Valid @RequestBody VectorCreationDTO vectorDTO) {

        AttackVector vector = campaignService.addAttackVector(
                id,
                vectorDTO.getName(),
                vectorDTO.getVectorType(),
                vectorDTO.getDescription(),
                vectorDTO.getTechnicalDetails(),
                vectorDTO.getPrevalence(),
                vectorDTO.getEffectiveness()
        );

        return ResponseEntity.ok(campaignMapper.toVectorDto(vector));
    }

    @PostMapping("/nodes/{id}/connect")
    @Operation(summary = "Connect nodes", 
              description = "Connects an infrastructure node to others in the network")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    public ResponseEntity<InfrastructureNodeDTO> connectNodes(
            @PathVariable Long id, 
            @Valid @RequestBody NodeConnectionDTO connectionDTO) {

        InfrastructureNode node = campaignService.connectNodes(id, connectionDTO.getTargetNodeIds());
        return ResponseEntity.ok(campaignMapper.toNodeDto(node));
    }
}
