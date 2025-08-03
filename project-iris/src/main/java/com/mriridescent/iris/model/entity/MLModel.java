package com.mriridescent.iris.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing a machine learning model used for email analysis.
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

    @Column(nullable = false)
    private String modelPath;

    @Column(nullable = false)
    private double accuracyScore;

    @Column(nullable = false)
    private double precisionScore;

    @Column(nullable = false)
    private double recallScore;

    @Column(nullable = false)
    private double f1Score;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime lastTrainedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public enum ModelType {
        CLASSIFIER,
        ANOMALY_DETECTOR,
        FEATURE_EXTRACTOR,
        CLUSTERING,
        NLP_MODEL
    }
}
