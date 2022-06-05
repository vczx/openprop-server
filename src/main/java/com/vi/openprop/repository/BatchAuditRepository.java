package com.vi.openprop.repository;

import com.vi.openprop.entity.BatchAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BatchAuditRepository extends JpaRepository<BatchAudit, Long> {
}
