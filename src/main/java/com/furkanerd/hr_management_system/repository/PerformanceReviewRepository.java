package com.furkanerd.hr_management_system.repository;

import com.furkanerd.hr_management_system.model.entity.PerformanceReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PerformanceReviewRepository extends JpaRepository<PerformanceReview, UUID> {

    List<PerformanceReview> findAllByEmployeeEmail(String email);

    Page<PerformanceReview> findAllByEmployeeId(UUID id, Pageable pageable);
}
