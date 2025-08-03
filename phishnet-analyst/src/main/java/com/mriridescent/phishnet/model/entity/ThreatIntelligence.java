package com.mriridescent.phishnet.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing threat intelligence information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "threat_intelligence")
public class ThreatIntelligence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String indicatorValue; // IP, domain, hash, etc.

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private IndicatorType indicatorType;

    @Column(nullable = false)
    private String source;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ThreatType threatType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ConfidenceLevel confidenceLevel;

    @ElementCollection
    @CollectionTable(name = "threat_tags", 
                     joinColumns = @JoinColumn(name = "threat_id"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @ManyToMany
    @JoinTable(
        name = "campaign_intelligence",
        joinColumns = @JoinColumn(name = "intelligence_id"),
        inverseJoinColumns = @JoinColumn(name = "campaign_id")
    )
    private Set<PhishingCampaign> relatedCampaigns = new HashSet<>();

    @Column(nullable = false)
    private LocalDateTime reportedAt;

    @Column
    private LocalDateTime lastObservedAt;

    @Column
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean active;

    @PrePersist
    protected void onCreate() {
        this.reportedAt = LocalDateTime.now();
        this.lastObservedAt = LocalDateTime.now();
        this.active = true;
    }

    public enum IndicatorType {
        IP_ADDRESS,
        DOMAIN,
        URL,
        EMAIL,
        FILE_HASH,
        USER_AGENT,
        PATTERN
    }

    public enum ThreatType {
        PHISHING,
        MALWARE,
        COMMAND_AND_CONTROL,
        EXFILTRATION,
        SCANNING,
        CREDENTIAL_THEFT,
        SOCIAL_ENGINEERING
    }

    public enum ConfidenceLevel {
        LOW,
        MEDIUM,
        HIGH,
        VERY_HIGH
    }
}
