package com.mriridescent.threatdetection.phishnet.model.entity;

import com.mriridescent.threatdetection.model.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a phishing campaign in PhishNet Analyst.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "phishing_campaigns")
public class PhishingCampaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CampaignStatus status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ThreatLevel threatLevel;

    @Column(nullable = false)
    private String targetSector;

    @Column(nullable = false)
    private LocalDateTime discoveredAt;

    @Column
    private LocalDateTime resolvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<InfrastructureNode> infrastructureNodes = new HashSet<>();

    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AttackVector> attackVectors = new HashSet<>();

    @Column(nullable = false, columnDefinition = "TEXT")
    private String tacticsAndTechniques;

    @Column(columnDefinition = "TEXT")
    private String mitigationStrategy;

    @Column
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum CampaignStatus {
        ACTIVE,
        MONITORING,
        MITIGATED,
        RESOLVED,
        ARCHIVED
    }

    public enum ThreatLevel {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }
}
