package com.acs.service.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "qa_pairs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QaPair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_question", columnDefinition = "TEXT")
    private String userQuestion;

    @Column(name = "human_answer", columnDefinition = "TEXT")
    private String humanAnswer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private ConversationSession session;

    @Column(name = "mined_at")
    private LocalDateTime minedAt;

    @Column(name = "promoted_to_knowledge")
    private Boolean promotedToKnowledge = false;

    @Column(name = "knowledge_entry_id")
    private Long knowledgeEntryId;

    @PrePersist
    protected void onCreate() {
        minedAt = LocalDateTime.now();
    }
}
