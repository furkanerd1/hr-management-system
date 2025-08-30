package com.furkanerd.hr_management_system.repository;

import com.furkanerd.hr_management_system.model.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, UUID> {

    boolean existsByEmployeeIdAndDate(UUID employeeId, LocalDate date);

    Optional<Attendance> findByEmployeeIdAndDate(UUID employeeId, LocalDate date);

    List<Attendance> findAllByEmployeeId(UUID employeeId);

    Page<Attendance> findAllByEmployeeId(UUID employeeId, Pageable pageable);
}
