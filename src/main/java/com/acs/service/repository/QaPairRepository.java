package com.acs.service.repository;

import com.acs.service.model.entity.QaPair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QaPairRepository extends JpaRepository<QaPair, Long> {
    List<QaPair> findByPromotedToKnowledgeFalse();
}
