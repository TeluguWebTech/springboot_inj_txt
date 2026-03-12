package com.kalyan.ingestion_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kalyan.ingestion_system.model.ProcessingAudit;

public interface ProcessingAuditRepository
        extends JpaRepository<ProcessingAudit, Long> {
}
