package com.furkanerd.hr_management_system.repository;

import com.furkanerd.hr_management_system.model.entity.Department;
import com.furkanerd.hr_management_system.model.entity.Employee;
import org.hibernate.query.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    
    Optional<Employee> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    List<Employee> findAllByDepartmentId(UUID departmentId);
}
