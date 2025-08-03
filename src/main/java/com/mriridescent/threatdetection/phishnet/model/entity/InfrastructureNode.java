package com.mriridescent.threatdetection.phishnet.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing an infrastructure node in a phishing campaign.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "infrastructure_nodes")
public class InfrastructureNode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    private PhishingCampaign campaign;

    @Column(nullable = false)
    private String identifier; // IP, domain, etc.

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NodeType nodeType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NodeStatus status;

    @ElementCollection
    @CollectionTable(name = "node_attributes", 
                     joinColumns = @JoinColumn(name = "node_id"))
    @Column(name = "attribute")
    private Set<String> attributes = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "node_connections",
        joinColumns = @JoinColumn(name = "source_node_id"),
        inverseJoinColumns = @JoinColumn(name = "target_node_id")
    )
    private Set<InfrastructureNode> connectedTo = new HashSet<>();

    @Column(nullable = false)
    private boolean isMalicious;

    @Column(nullable = false)
    private LocalDateTime discoveredAt;
package com.mriridescent.threatdetection.phishnet.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing an infrastructure node in a phishing campaign.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "infrastructure_nodes")
public class InfrastructureNode {

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
    private NodeType nodeType;

    @Column(nullable = false)
    private String identifier; // URL, IP, domain, etc.

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NodeStatus status;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String technicalDetails;

    @Column(columnDefinition = "TEXT")
    private String mitigationSteps;

    @Column(nullable = false)
    private LocalDateTime discoveredAt;

    @Column
    private LocalDateTime lastCheckedAt;

    @PrePersist
    protected void onCreate() {
        this.discoveredAt = LocalDateTime.now();
        this.lastCheckedAt = LocalDateTime.now();
    }

    public enum NodeType {
        DOMAIN,
        IP_ADDRESS,
        URL,
        EMAIL_SERVER,
        REDIRECT_SERVER,
        LANDING_PAGE,
        COMMAND_CONTROL,
        HOSTING_SERVER,
        DATA_COLLECTION,
        DISTRIBUTION_POINT,
        OTHER
    }

    public enum NodeStatus {
        ACTIVE,
        INACTIVE,
        MITIGATED,
        REDIRECTED,
        SINKHOLED,
        UNKNOWN
    }
}
    @Column
    private LocalDateTime lastSeenAt;

    @PrePersist
    protected void onCreate() {
        this.discoveredAt = LocalDateTime.now();
        this.lastSeenAt = LocalDateTime.now();
    }

    public enum NodeType {
        DOMAIN,
        IP_ADDRESS,
        SERVER,
        EMAIL_ADDRESS,
        WEBSITE,
        DROPZONE,
        C2_SERVER,
        REDIRECTOR
    }

    public enum NodeStatus {
        ACTIVE,
        SUSPENDED,
        TAKEN_DOWN,
        UNKNOWN
    }
}
