package com.mriridescent.threatdetection.iris.model.entity;

import com.mriridescent.threatdetection.model.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing a machine learning model used in Project Iris.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ml_models")
public class MLModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String version;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ModelType modelType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private double accuracy;

    @Column(nullable = false)
    private double precision;

    @Column(nullable = false)
    private double recall;

    @Column(nullable = false)
    private double f1Score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime lastTrainedAt;

    @Column(nullable = false)
    private boolean active;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public enum ModelType {
        NEURAL_NETWORK,
        RANDOM_FOREST,
        GRADIENT_BOOSTING,
        SVM,
        ENSEMBLE
    }
}
