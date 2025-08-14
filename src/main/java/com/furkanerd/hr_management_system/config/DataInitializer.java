package com.furkanerd.hr_management_system.config;

import com.furkanerd.hr_management_system.model.entity.Department;
import com.furkanerd.hr_management_system.model.entity.Employee;
import com.furkanerd.hr_management_system.model.entity.Position;
import com.furkanerd.hr_management_system.model.entity.Salary;
import com.furkanerd.hr_management_system.model.enums.EmployeeRoleEnum;
import com.furkanerd.hr_management_system.model.enums.EmployeeStatusEnum;
import com.furkanerd.hr_management_system.repository.DepartmentRepository;
import com.furkanerd.hr_management_system.repository.EmployeeRepository;
import com.furkanerd.hr_management_system.repository.PositionRepository;
import com.furkanerd.hr_management_system.repository.SalaryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final EmployeeRepository employeeRepository;
    private final SalaryRepository salaryRepository;

    public DataInitializer(DepartmentRepository departmentRepository, PositionRepository positionRepository, EmployeeRepository employeeRepository, SalaryRepository salaryRepository) {
        this.departmentRepository = departmentRepository;
        this.positionRepository = positionRepository;
        this.employeeRepository = employeeRepository;
        this.salaryRepository = salaryRepository;
    }


    @Override
    public void run(String... args) throws Exception {
        if (departmentRepository.count() == 0) {
            Department it = Department.builder()
                    .name("IT")
                    .description("Information Technology Department")
                    .build();
            Department hr = Department.builder()
                    .name("HR")
                    .description("Human Resources Department")
                    .build();
            Department finance = Department.builder()
                    .name("Finance")
                    .description("Finance Department")
                    .build();

            departmentRepository.saveAll(List.of(it, hr, finance));
        }
        // Positions
        if (positionRepository.count() == 0) {
            Position softwareEngineer = Position.builder()
                    .title("Software Engineer")
                    .description("Develops software applications")
                    .build();
            Position qaEngineer = Position.builder()
                    .title("QA Engineer")
                    .description("Tests software applications")
                    .build();
            Position projectManager = Position.builder()
                    .title("Project Manager")
                    .description("Manages project timelines and resources")
                    .build();

            positionRepository.saveAll(List.of(softwareEngineer, qaEngineer, projectManager));
        }

        // Employees
        if (employeeRepository.count() == 0) {
            // Department ve Position referanslarını DB’den alıyoruz
            Department defaultDept = departmentRepository.findByName("IT")
                    .orElseThrow(() -> new RuntimeException("Default department not found"));

            Position software = positionRepository.findByTitle("Software Engineer")
                    .orElseThrow(() -> new RuntimeException("Software Engineer position not found"));

            Position qaEngineer = positionRepository.findByTitle("QA Engineer")
                    .orElseThrow(() -> new RuntimeException("QA Engineer position not found"));

            Employee emp1 = Employee.builder()
                    .firstName("Ali")
                    .lastName("Veli")
                    .email("ali.veli@example.com")
                    .hireDate(LocalDate.of(2023, 1, 1))
                    .birthDate(LocalDate.of(1990, 5, 5))
                    .role(EmployeeRoleEnum.EMPLOYEE)
                    .status(EmployeeStatusEnum.ACTIVE)
                    .department(defaultDept)
                    .position(software)
                    .build();

            Employee emp2 = Employee.builder()
                    .firstName("Ayşe")
                    .lastName("Yılmaz")
                    .email("ayse.yilmaz@example.com")
                    .hireDate(LocalDate.of(2022, 3, 15))
                    .birthDate(LocalDate.of(1992, 8, 20))
                    .role(EmployeeRoleEnum.MANAGER)
                    .status(EmployeeStatusEnum.ACTIVE)
                    .department(defaultDept)
                    .position(qaEngineer)
                    .build();

            employeeRepository.saveAll(List.of(emp1, emp2));
        }

        // Salaries
        if (salaryRepository.count() == 0) {
            // Employee’leri DB’den alıyoruz
            Employee emp1 = employeeRepository.findByEmail("ali.veli@example.com")
                    .orElseThrow(() -> new RuntimeException("Employee Ali Veli not found"));
            Employee emp2 = employeeRepository.findByEmail("ayse.yilmaz@example.com")
                    .orElseThrow(() -> new RuntimeException("Employee Ayşe Yılmaz not found"));

            Salary salary1 = Salary.builder()
                    .employee(emp1)
                    .salary(new BigDecimal("5000.00"))
                    .bonus(new BigDecimal("500.00"))
                    .effectiveDate(LocalDate.now().plusDays(1)) // geleceğe tarih
                    .build();

            Salary salary2 = Salary.builder()
                    .employee(emp2)
                    .salary(new BigDecimal("6000.00"))
                    .bonus(new BigDecimal("800.00"))
                    .effectiveDate(LocalDate.now().plusDays(1))
                    .build();

            salaryRepository.saveAll(List.of(salary1, salary2));
        }
    }
}
