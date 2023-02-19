package com.currencyExchange.audit.reposistory;

import com.currencyExchange.audit.models.Audit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditReposistory extends JpaRepository<Audit, Long> {
}
