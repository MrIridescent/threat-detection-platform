package com.mriridescent.threatdetection.iris.service;

import com.mriridescent.threatdetection.iris.dto.AnalysisRequestDto;
import com.mriridescent.threatdetection.iris.dto.AnalysisResultDto;
import com.mriridescent.threatdetection.iris.model.entity.MLModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IrisAnalysisServiceTest {

    @Mock
    private MLModelService mlModelService;

    @InjectMocks
    private IrisAnalysisService analysisService;

    private AnalysisRequestDto testRequest;
    private MLModel testModel;

    @BeforeEach
    void setUp() {
        // Set threat threshold
        ReflectionTestUtils.setField(analysisService, "threatThreshold", 0.75);

        // Create test request
        testRequest = AnalysisRequestDto.builder()
                .subject("Test Email Subject")
                .sender("test@example.com")
                .recipients(new String[]{"recipient@example.org"})
                .body("This is a test email body")
                .headers(new HashMap<>())
                .build();

        // Create test ML model
        testModel = MLModel.builder()
                .id(1L)
                .name("Test Model")
                .version("1.0.0")
                .modelType(MLModel.ModelType.NEURAL_NETWORK)
                .description("Test model for unit tests")
                .filePath("/models/test.model")
                .accuracy(0.9)
                .precision(0.85)
                .recall(0.88)
                .f1Score(0.86)
                .createdAt(LocalDateTime.now())
                .active(true)
                .build();
    }

    @Test
    void shouldAnalyzeEmailWithActiveModels() {
        // Given
        List<MLModel> activeModels = Arrays.asList(testModel);
        when(mlModelService.getActiveModels()).thenReturn(activeModels);

        // When
        AnalysisResultDto result = analysisService.analyzeEmail(testRequest);

        // Then
        assertNotNull(result);
        assertEquals("Test Email Subject", result.getEmailSubject());
        assertEquals("test@example.com", result.getEmailSender());
        assertTrue(result.getModelScores().containsKey("Test Model"));
        verify(mlModelService, times(1)).getActiveModels();
    }

    @Test
    void shouldHandleNoActiveModels() {
        // Given
        when(mlModelService.getActiveModels()).thenReturn(List.of());

        // When
        AnalysisResultDto result = analysisService.analyzeEmail(testRequest);

        // Then
        assertNotNull(result);
        assertEquals("Test Email Subject", result.getEmailSubject());
        assertEquals(0.0, result.getThreatScore());
        assertEquals("MINIMAL", result.getThreatLevel());
        assertTrue(result.getDetectedThreats().contains("No active models available"));
        verify(mlModelService, times(1)).getActiveModels();
    }

    @Test
    void shouldReturnRecentAnalyses() {
        // Given - first analyze an email to store a result
        List<MLModel> activeModels = Arrays.asList(testModel);
        when(mlModelService.getActiveModels()).thenReturn(activeModels);
        analysisService.analyzeEmail(testRequest);

        // When
        List<AnalysisResultDto> results = analysisService.getRecentAnalyses(10);

        // Then
        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals("Test Email Subject", results.get(0).getEmailSubject());
    }

    @Test
    void shouldSubmitFeedbackOnAnalysis() {
        // Given - first analyze an email to store a result
        List<MLModel> activeModels = Arrays.asList(testModel);
        when(mlModelService.getActiveModels()).thenReturn(activeModels);
        AnalysisResultDto analysis = analysisService.analyzeEmail(testRequest);
        Long analysisId = analysis.getId();

        // When
        AnalysisResultDto result = analysisService.submitFeedback(analysisId, false);

        // Then
        assertNotNull(result);
        // If the threat score was high and feedback says it's not a threat, it's a false positive
        if (result.getThreatScore() > 0.75) {
            assertTrue(result.isFalsePositiveFeedback());
        }
    }

    @Test
    void shouldThrowExceptionForInvalidAnalysisId() {
        // Given an invalid analysis ID
        Long invalidId = 999L;

        // When/Then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            analysisService.submitFeedback(invalidId, true);
        });

        assertTrue(exception.getMessage().contains("Analysis not found with id: " + invalidId));
    }
}
