package com.mriridescent.threatdetection.phishnet.service;

import com.mriridescent.threatdetection.phishnet.model.entity.PhishingCampaign;
import com.mriridescent.threatdetection.phishnet.repository.PhishingCampaignRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PhishingCampaignServiceTest {

    @Mock
    private PhishingCampaignRepository campaignRepository;

    @InjectMocks
    private PhishingCampaignService campaignService;

    private PhishingCampaign testCampaign;

    @BeforeEach
    void setUp() {
        testCampaign = PhishingCampaign.builder()
                .id(1L)
                .name("Test Campaign")
                .description("Test campaign for unit tests")
                .status(PhishingCampaign.CampaignStatus.ACTIVE)
                .severity(PhishingCampaign.Severity.HIGH)
                .targetDemographic("Test demographic")
                .estimatedReach(1000)
                .originRegion("Test region")
                .technicalDetails("Test technical details")
                .createdAt(LocalDateTime.now())
                .lastUpdatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldGetAllCampaigns() {
        // Given
        List<PhishingCampaign> campaigns = Arrays.asList(testCampaign);
        when(campaignRepository.findAll()).thenReturn(campaigns);

        // When
        List<PhishingCampaign> result = campaignService.getAllCampaigns();

        // Then
        assertEquals(1, result.size());
        assertEquals("Test Campaign", result.get(0).getName());
        verify(campaignRepository, times(1)).findAll();
    }

    @Test
    void shouldGetActiveCampaigns() {
        // Given
        List<PhishingCampaign> campaigns = Arrays.asList(testCampaign);
        when(campaignRepository.findByStatusIn(anyList())).thenReturn(campaigns);

        // When
        List<PhishingCampaign> result = campaignService.getActiveCampaigns();

        // Then
        assertEquals(1, result.size());
        assertEquals(PhishingCampaign.CampaignStatus.ACTIVE, result.get(0).getStatus());
        verify(campaignRepository, times(1)).findByStatusIn(anyList());
    }

    @Test
    void shouldGetCampaignById() {
        // Given
        when(campaignRepository.findById(1L)).thenReturn(Optional.of(testCampaign));

        // When
        Optional<PhishingCampaign> result = campaignService.getCampaignById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Test Campaign", result.get().getName());
        verify(campaignRepository, times(1)).findById(1L);
    }

    @Test
    void shouldCreateCampaign() {
        // Given
        when(campaignRepository.save(any(PhishingCampaign.class))).thenReturn(testCampaign);

        // When
        PhishingCampaign result = campaignService.createCampaign(testCampaign);

        // Then
        assertEquals("Test Campaign", result.getName());
        verify(campaignRepository, times(1)).save(any(PhishingCampaign.class));
    }

    @Test
    void shouldUpdateCampaign() {
        // Given
        PhishingCampaign updatedCampaign = PhishingCampaign.builder()
                .id(1L)
                .name("Updated Campaign")
                .description("Updated description")
                .status(PhishingCampaign.CampaignStatus.ESCALATING)
                .severity(PhishingCampaign.Severity.CRITICAL)
                .targetDemographic("Updated demographic")
                .estimatedReach(2000)
                .originRegion("Updated region")
                .technicalDetails("Updated technical details")
                .build();

        when(campaignRepository.findById(1L)).thenReturn(Optional.of(testCampaign));
        when(campaignRepository.save(any(PhishingCampaign.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        PhishingCampaign result = campaignService.updateCampaign(1L, updatedCampaign);

        // Then
        assertEquals("Updated Campaign", result.getName());
        assertEquals(PhishingCampaign.CampaignStatus.ESCALATING, result.getStatus());
        assertEquals(PhishingCampaign.Severity.CRITICAL, result.getSeverity());
        verify(campaignRepository, times(1)).findById(1L);
        verify(campaignRepository, times(1)).save(any(PhishingCampaign.class));
    }

    @Test
    void shouldUpdateCampaignStatus() {
        // Given
        when(campaignRepository.findById(1L)).thenReturn(Optional.of(testCampaign));
        when(campaignRepository.save(any(PhishingCampaign.class))).thenAnswer(invocation -> {
            PhishingCampaign campaign = invocation.getArgument(0);
            campaign.setStatus(PhishingCampaign.CampaignStatus.MITIGATED);
            return campaign;
        });

        // When
        PhishingCampaign result = campaignService.updateCampaignStatus(1L, PhishingCampaign.CampaignStatus.MITIGATED);

        // Then
        assertEquals(PhishingCampaign.CampaignStatus.MITIGATED, result.getStatus());
        verify(campaignRepository, times(1)).findById(1L);
        verify(campaignRepository, times(1)).save(any(PhishingCampaign.class));
    }

    @Test
    void shouldUpdateCampaignSeverity() {
        // Given
        when(campaignRepository.findById(1L)).thenReturn(Optional.of(testCampaign));
        when(campaignRepository.save(any(PhishingCampaign.class))).thenAnswer(invocation -> {
            PhishingCampaign campaign = invocation.getArgument(0);
            campaign.setSeverity(PhishingCampaign.Severity.MEDIUM);
            return campaign;
        });

        // When
        PhishingCampaign result = campaignService.updateCampaignSeverity(1L, PhishingCampaign.Severity.MEDIUM);

        // Then
        assertEquals(PhishingCampaign.Severity.MEDIUM, result.getSeverity());
        verify(campaignRepository, times(1)).findById(1L);
        verify(campaignRepository, times(1)).save(any(PhishingCampaign.class));
    }
}
