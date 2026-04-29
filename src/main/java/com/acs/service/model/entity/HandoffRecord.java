package com.acs.service.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "handoff_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HandoffRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "session_id")
    private ConversationSession session;

    @Column(name = "trigger_reason")
    private String triggerReason;

    @Column(name = "emotion_detected")
    private String emotionDetected;

    @Column(name = "intent_detected")
    private String intentDetected;

    @Column(name = "summary_json", columnDefinition = "TEXT")
    private String summaryJson;

    @Column(name = "crm_push_status")
    private String crmPushStatus;

    @Column(name = "human_resolution", columnDefinition = "TEXT")
    private String humanResolution;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
