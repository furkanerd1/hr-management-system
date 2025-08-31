package com.furkanerd.hr_management_system.repository;


import com.furkanerd.hr_management_system.model.entity.Salary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, UUID> {

    Page<Salary> findAllByEmployeeEmail(String employeeEmail,Pageable pageable);

    Page<Salary> findAllByEmployeeId(UUID employeeId, Pageable pageable);
}
