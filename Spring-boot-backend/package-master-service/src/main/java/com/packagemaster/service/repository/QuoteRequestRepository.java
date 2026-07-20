package com.packagemaster.service.repository;

import com.packagemaster.service.model.QuoteRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface QuoteRequestRepository extends JpaRepository<QuoteRequest, Integer>, JpaSpecificationExecutor<QuoteRequest> {
}
