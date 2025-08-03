package com.mriridescent.threatdetection.phishnet.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing an attack vector in a phishing campaign.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "attack_vectors")
public class AttackVector {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    private PhishingCampaign campaign;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private VectorType vectorType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private VectorStatus status;

    @Column(nullable = false)
    private int prevalence; // 1-10 scale

    @Column(nullable = false)
    private int effectiveness; // 1-10 scale

    @Column(nullable = false, columnDefinition = "TEXT")
    private String technicalDetails;

    @Column(columnDefinition = "TEXT")
    private String mitigationSteps;

    @Column(nullable = false)
    private LocalDateTime identifiedAt;

    @Column
    private LocalDateTime lastObservedAt;

    @PrePersist
    protected void onCreate() {
        this.identifiedAt = LocalDateTime.now();
        this.lastObservedAt = LocalDateTime.now();
    }

    public enum VectorType {
        EMAIL,
        WEBSITE,
        SOCIAL_MEDIA,
        SMS,
        MESSAGING_APP,
        MALICIOUS_DOCUMENT,
        SOCIAL_ENGINEERING,
        OTHER
    }

    public enum VectorStatus {
        ACTIVE,
        MITIGATED,
        BLOCKED,
        EVOLVED
    }
}
