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
package com.mriridescent.threatdetection.phishnet.model.entity;

import com.mriridescent.threatdetection.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private Severity severity;

    @Column(nullable = false)
    private String targetDemographic;

    @Column(nullable = false)
    private int estimatedReach;

    @Column(nullable = false)
    private String originRegion;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String technicalDetails;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime lastUpdatedAt;

    @Column
    private LocalDateTime firstObservedAt;

    @Column
    private LocalDateTime lastObservedAt;

    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InfrastructureNode> infrastructureNodes = new ArrayList<>();

    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AttackVector> attackVectors = new ArrayList<>();

    /**
     * Add an infrastructure node to the campaign
     *
     * @param node The node to add
     */
    public void addInfrastructureNode(InfrastructureNode node) {
        infrastructureNodes.add(node);
        node.setCampaign(this);
    }

    /**
     * Remove an infrastructure node from the campaign
     *
     * @param node The node to remove
     */
    public void removeInfrastructureNode(InfrastructureNode node) {
        infrastructureNodes.remove(node);
        node.setCampaign(null);
    }

    /**
     * Add an attack vector to the campaign
     *
     * @param vector The vector to add
     */
    public void addAttackVector(AttackVector vector) {
        attackVectors.add(vector);
        vector.setCampaign(this);
    }

    /**
     * Remove an attack vector from the campaign
     *
     * @param vector The vector to remove
     */
    public void removeAttackVector(AttackVector vector) {
        attackVectors.remove(vector);
        vector.setCampaign(null);
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.lastUpdatedAt = LocalDateTime.now();
    }

    public enum CampaignStatus {
        ACTIVE,
        ESCALATING,
        CONTAINED,
        MITIGATED,
        DORMANT,
        HISTORICAL
    }

    public enum Severity {
        CRITICAL,
        HIGH,
        MEDIUM,
        LOW,
        INFORMATIONAL
    }
}
    @Column(nullable = false, columnDefinition = "TEXT")
    private String tacticsAndTechniques;

    @Column(columnDefinition = "TEXT")
    private String mitigationStrategy;

    @Column(nullable = false)
    private LocalDateTime createdAt;

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
