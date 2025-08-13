package com.furkanerd.hr_management_system.config;

import com.furkanerd.hr_management_system.model.entity.Department;
import com.furkanerd.hr_management_system.repository.DepartmentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final DepartmentRepository departmentRepository;

    public DataInitializer(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
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
    }
}
