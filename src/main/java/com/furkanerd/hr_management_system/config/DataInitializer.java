package com.furkanerd.hr_management_system.config;

import com.furkanerd.hr_management_system.model.entity.Department;
import com.furkanerd.hr_management_system.model.entity.Position;
import com.furkanerd.hr_management_system.repository.DepartmentRepository;
import com.furkanerd.hr_management_system.repository.PositionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;

    public DataInitializer(DepartmentRepository departmentRepository, PositionRepository positionRepository) {
        this.departmentRepository = departmentRepository;
        this.positionRepository = positionRepository;
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
    }
}
