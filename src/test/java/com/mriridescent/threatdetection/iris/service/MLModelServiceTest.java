package com.mriridescent.threatdetection.iris.service;

import com.mriridescent.threatdetection.iris.model.entity.MLModel;
import com.mriridescent.threatdetection.iris.repository.MLModelRepository;
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
class MLModelServiceTest {

    @Mock
    private MLModelRepository mlModelRepository;

    @InjectMocks
    private MLModelService mlModelService;

    private MLModel testModel;

    @BeforeEach
    void setUp() {
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
    void shouldGetAllModels() {
        // Given
        List<MLModel> models = Arrays.asList(testModel);
        when(mlModelRepository.findAll()).thenReturn(models);

        // When
        List<MLModel> result = mlModelService.getAllModels();

        // Then
        assertEquals(1, result.size());
        assertEquals("Test Model", result.get(0).getName());
        verify(mlModelRepository, times(1)).findAll();
    }

    @Test
    void shouldGetActiveModels() {
        // Given
        List<MLModel> models = Arrays.asList(testModel);
        when(mlModelRepository.findByActiveTrue()).thenReturn(models);

        // When
        List<MLModel> result = mlModelService.getActiveModels();

        // Then
        assertEquals(1, result.size());
        assertTrue(result.get(0).isActive());
        verify(mlModelRepository, times(1)).findByActiveTrue();
    }

    @Test
    void shouldGetModelById() {
        // Given
        when(mlModelRepository.findById(1L)).thenReturn(Optional.of(testModel));

        // When
        Optional<MLModel> result = mlModelService.getModelById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Test Model", result.get().getName());
        verify(mlModelRepository, times(1)).findById(1L);
    }

    @Test
    void shouldCreateModel() {
        // Given
        when(mlModelRepository.save(any(MLModel.class))).thenReturn(testModel);

        // When
        MLModel result = mlModelService.createModel(testModel);

        // Then
        assertEquals("Test Model", result.getName());
        verify(mlModelRepository, times(1)).save(any(MLModel.class));
    }

    @Test
    void shouldUpdateModel() {
        // Given
        MLModel updatedModel = testModel.toBuilder()
                .name("Updated Model")
                .version("1.0.1")
                .build();

        when(mlModelRepository.findById(1L)).thenReturn(Optional.of(testModel));
        when(mlModelRepository.save(any(MLModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        MLModel result = mlModelService.updateModel(1L, updatedModel);

        // Then
        assertEquals("Updated Model", result.getName());
        assertEquals("1.0.1", result.getVersion());
        verify(mlModelRepository, times(1)).findById(1L);
        verify(mlModelRepository, times(1)).save(any(MLModel.class));
    }

    @Test
    void shouldActivateModel() {
        // Given
        testModel.setActive(false);
        when(mlModelRepository.findById(1L)).thenReturn(Optional.of(testModel));
        when(mlModelRepository.save(any(MLModel.class))).thenAnswer(invocation -> {
            MLModel model = invocation.getArgument(0);
            model.setActive(true);
            return model;
        });

        // When
        MLModel result = mlModelService.activateModel(1L);

        // Then
        assertTrue(result.isActive());
        verify(mlModelRepository, times(1)).findById(1L);
        verify(mlModelRepository, times(1)).save(any(MLModel.class));
    }

    @Test
    void shouldDeactivateModel() {
        // Given
        when(mlModelRepository.findById(1L)).thenReturn(Optional.of(testModel));
        when(mlModelRepository.save(any(MLModel.class))).thenAnswer(invocation -> {
            MLModel model = invocation.getArgument(0);
            model.setActive(false);
            return model;
        });

        // When
        MLModel result = mlModelService.deactivateModel(1L);

        // Then
        assertFalse(result.isActive());
        verify(mlModelRepository, times(1)).findById(1L);
        verify(mlModelRepository, times(1)).save(any(MLModel.class));
    }
}
