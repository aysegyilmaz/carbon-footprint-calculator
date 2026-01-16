package com.example.carbon_calculator.repository;

import com.example.carbon_calculator.model.CarbonRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CarbonRepository extends JpaRepository<CarbonRecord, Long> {
    List<CarbonRecord> findByRecordDateBetween(LocalDateTime start, LocalDateTime end);
}