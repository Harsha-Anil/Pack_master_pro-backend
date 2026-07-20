package com.packagemaster.service.repository;

import com.packagemaster.service.model.Enquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EnquiryRepository extends JpaRepository<Enquiry, Integer>, JpaSpecificationExecutor<Enquiry> {
}
