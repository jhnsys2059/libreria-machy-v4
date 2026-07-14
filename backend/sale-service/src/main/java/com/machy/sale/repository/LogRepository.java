package com.machy.sale.repository;

import com.machy.sale.entity.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface LogRepository extends JpaRepository<LogEntry, UUID> {
}
