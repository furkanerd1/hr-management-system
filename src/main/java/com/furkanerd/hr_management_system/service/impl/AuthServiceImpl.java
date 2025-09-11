package com.furkanerd.hr_management_system.service.impl;

import com.furkanerd.hr_management_system.exception.*;
import com.furkanerd.hr_management_system.model.dto.request.LoginRequest;
import com.furkanerd.hr_management_system.model.dto.request.RegisterRequest;
import com.furkanerd.hr_management_system.model.dto.response.auth.LoginResponse;
import com.furkanerd.hr_management_system.model.dto.response.auth.RegisterResponse;
import com.furkanerd.hr_management_system.model.entity.Department;
import com.furkanerd.hr_management_system.model.entity.Employee;
import com.furkanerd.hr_management_system.model.entity.Position;
import com.furkanerd.hr_management_system.model.enums.EmployeeRoleEnum;
import com.furkanerd.hr_management_system.repository.EmployeeRepository;
import com.furkanerd.hr_management_system.security.JwtUtil;
import com.furkanerd.hr_management_system.service.AuthService;
import com.furkanerd.hr_management_system.service.DepartmentService;
import com.furkanerd.hr_management_system.service.PositionService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmployeeRepository employeeRepository;
    private final DepartmentService departmentService;
    private final PositionService positionService;

    public AuthServiceImpl(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, EmployeeRepository employeeRepository, DepartmentService departmentService, PositionService positionService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.employeeRepository = employeeRepository;
        this.departmentService = departmentService;
        this.positionService = positionService;
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.email(),
                            loginRequest.password()
                    )
            );

            // Create jwt Token
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.email());
            String token = jwtUtil.generateToken(userDetails);

            // Get Employee Information
            Employee employee = employeeRepository.findByEmail(loginRequest.email())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

            return LoginResponse.builder()
                    .token(token)
                    .employeeId(employee.getId())
                    .email(userDetails.getUsername())
                    .firstName(employee.getFirstName())
                    .lastName(employee.getLastName())
                    .role(employee.getRole())
                    .roles(userDetails.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toList()))
                    .mustChangePassword(employee.isMustChangePassword())
                    .build();
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid username or password", e);
        }
    }

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest registerRequest) {

        if (employeeRepository.existsByEmail(registerRequest.email())) {
            throw new EmailAlreadyExistsException("Email already exists: " + registerRequest.email());
        }

        if (registerRequest.phone() != null &&
                !registerRequest.phone().trim().isEmpty() &&
                employeeRepository.existsByPhone(registerRequest.phone())) {
            throw new PhoneNumberAlreadyExistsException("Phone number already exists: " + registerRequest.phone());
        }

        // Set the default password for the first time
        String tempPassword = generateTempPassword();
        String encodedPassword = passwordEncoder.encode(tempPassword);

        Employee employee = Employee.builder()
                .firstName(registerRequest.firstName())
                .lastName(registerRequest.lastName())
                .email(registerRequest.email())
                .password(encodedPassword)
                .phone(registerRequest.phone())
                .hireDate(registerRequest.hireDate())
                .birthDate(registerRequest.birthDate())
                .address(registerRequest.address())
                .role(registerRequest.role())
                .status(registerRequest.status())
                .mustChangePassword(true)
                .vacationBalance(20)
                .maternityBalance(112)
                .build();

        // Set Department
        if (registerRequest.departmentId() != null) {
            Department department = departmentService.getDepartmentEntityById(registerRequest.departmentId());
            employee.setDepartment(department);
        }

        // Set position
        if (registerRequest.positionId() != null) {
            Position position = positionService.getPositionEntityById(registerRequest.positionId());
            employee.setPosition(position);
        }

        // Set manager
        if (registerRequest.managerId() != null) {
            Employee manager = employeeRepository.findById(registerRequest.managerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found with id: " + registerRequest.managerId()));

            if (manager.getRole() != EmployeeRoleEnum.MANAGER) {
                throw new InvalidRoleException("Selected manager must have MANAGER");
            }

            employee.setManager(manager);
        }
        Employee savedEmployee = employeeRepository.save(employee);
        return new RegisterResponse(
                savedEmployee.getId(),
                savedEmployee.getEmail(),
                savedEmployee.getFirstName(),
                savedEmployee.getLastName(),
                savedEmployee.getRole(),
                tempPassword,
                List.of(savedEmployee.getRole().name())
        );
    }

    @Override
    @Transactional
    public void changePassword(String email, String oldPassword, String newPassword) {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        if (!passwordEncoder.matches(oldPassword, employee.getPassword())) {
            throw new IncorrectPasswordException("Invalid current password");
        }

        employee.setPassword(passwordEncoder.encode(newPassword));
        employee.setMustChangePassword(false);
        employeeRepository.save(employee);
    }

    public boolean isTokenValid(String token) {
        try {
            String username = jwtUtil.extractUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            return jwtUtil.validateToken(token, userDetails);
        } catch (Exception e) {
            return false;
        }
    }

    private String generateTempPassword() {
        // Format: EMP + 4 digit random
        return "EMP" + String.format("%04d", new Random().nextInt(10000));
    }
}
