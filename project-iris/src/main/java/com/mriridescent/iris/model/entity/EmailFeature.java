package com.mriridescent.iris.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing an extracted feature from an email for ML analysis.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "email_features")
public class EmailFeature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email_analysis_id", nullable = false)
    private EmailAnalysis emailAnalysis;

    @Column(nullable = false)
    private String featureName;

    @Column(nullable = false)
    private String featureValue;

    @Column(nullable = false)
    private double weight;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FeatureType featureType;

    @Column(nullable = false)
    private LocalDateTime extractedAt;

    @PrePersist
    protected void onCreate() {
        this.extractedAt = LocalDateTime.now();
    }

    public enum FeatureType {
        HEADER,
        CONTENT,
        METADATA,
        SEMANTIC,
        NETWORK,
        BEHAVIORAL
    }
}
