package com.acs.service.repository;

import com.acs.service.model.entity.ConversationSession;
import com.acs.service.model.enums.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationSessionRepository extends JpaRepository<ConversationSession, Long> {
    Optional<ConversationSession> findBySessionKey(String sessionKey);
    List<ConversationSession> findByStatus(SessionStatus status);
}
