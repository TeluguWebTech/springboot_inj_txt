package com.kalyan.ingestion_system.repository;

import com.kalyan.ingestion_system.model.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxEvent, Long> {

    List<OutboxEvent> findByStatus(String status);
}