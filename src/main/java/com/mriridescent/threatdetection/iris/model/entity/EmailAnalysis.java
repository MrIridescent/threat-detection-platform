package com.mriridescent.threatdetection.iris.model.entity;

import com.mriridescent.threatdetection.model.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Entity representing an email analysis performed by Project Iris.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "email_analyses")
public class EmailAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String emailId;

    @Column(nullable = false)
    private String sender;

    @Column(nullable = false)
    private String recipient;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ElementCollection
    @CollectionTable(name = "email_metadata", 
                     joinColumns = @JoinColumn(name = "analysis_id"))
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    private Map<String, String> metadata = new HashMap<>();

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ThreatLevel threatLevel;

    @Column(nullable = false)
    private double threatScore;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String analysisDetails;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analyzed_by")
    private User analyzedBy;

    @Column(nullable = false)
    private LocalDateTime analyzedAt;

    @Column
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean falsePositive;

    @PrePersist
    protected void onCreate() {
        this.analyzedAt = LocalDateTime.now();
        this.falsePositive = false;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum ThreatLevel {
        SAFE,
        SUSPICIOUS,
        DANGEROUS,
        CRITICAL
    }
}
