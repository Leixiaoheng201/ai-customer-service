package com.acs.service.model.entity;

import com.acs.service.model.enums.SessionStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conversation_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_key", unique = true, nullable = false)
    private String sessionKey;

    @Column(name = "user_id")
    private String userId;

    @Enumerated(EnumType.STRING)
    private SessionStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "handoff_triggered")
    private Boolean handoffTriggered = false;

    @Column(name = "consecutive_failures")
    private Integer consecutiveFailures = 0;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<UserMessage> messages = new ArrayList<>();

    @Column(name = "crm_summary_json", columnDefinition = "TEXT")
    private String crmSummaryJson;

    @Column(name = "human_agent_id")
    private String humanAgentId;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) status = SessionStatus.ACTIVE;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
