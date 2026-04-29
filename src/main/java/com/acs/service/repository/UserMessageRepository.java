package com.acs.service.repository;

import com.acs.service.model.entity.UserMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMessageRepository extends JpaRepository<UserMessage, Long> {
    List<UserMessage> findBySessionIdOrderByCreatedAtAsc(Long sessionId);
}
