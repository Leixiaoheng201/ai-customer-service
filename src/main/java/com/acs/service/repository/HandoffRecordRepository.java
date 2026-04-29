package com.acs.service.repository;

import com.acs.service.model.entity.HandoffRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HandoffRecordRepository extends JpaRepository<HandoffRecord, Long> {
    List<HandoffRecord> findByHumanResolutionIsNull();
    List<HandoffRecord> findByHumanResolutionIsNotNull();
}
