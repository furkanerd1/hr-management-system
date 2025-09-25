package com.furkanerd.hr_management_system.service.auth.impl;

import com.furkanerd.hr_management_system.exception.custom.*;
import com.furkanerd.hr_management_system.model.dto.request.LoginRequest;
import com.furkanerd.hr_management_system.model.dto.request.RegisterRequest;
import com.furkanerd.hr_management_system.model.dto.response.auth.LoginResponse;
import com.furkanerd.hr_management_system.model.dto.response.auth.RegisterResponse;
import com.furkanerd.hr_management_system.model.entity.Employee;
import com.furkanerd.hr_management_system.model.enums.EmployeeRoleEnum;
import com.furkanerd.hr_management_system.repository.EmployeeRepository;
import com.furkanerd.hr_management_system.security.JwtUtil;
import com.furkanerd.hr_management_system.service.department.DepartmentService;
import com.furkanerd.hr_management_system.service.notification.NotificationService;
import com.furkanerd.hr_management_system.service.position.PositionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    @InjectMocks
    private AuthServiceImpl service;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentService departmentService;

    @Mock
    private PositionService positionService;

    @Mock
    private NotificationService notificationService;

    private Employee employee;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        employee = Employee.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .password("encodedPass")
                .role(EmployeeRoleEnum.EMPLOYEE)
                .mustChangePassword(true)
                .build();
    }

    // LOGIN
    @Test
    void login_success() {
        LoginRequest loginRequest = new LoginRequest("john@example.com", "password");

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(employee.getEmail());
        when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());

        when(userDetailsService.loadUserByUsername(employee.getEmail())).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("jwtToken");
        when(employeeRepository.findByEmail(employee.getEmail())).thenReturn(Optional.of(employee));

        LoginResponse response = service.login(loginRequest);
        assertNotNull(response);
        assertEquals("jwtToken", response.token());
    }

    @Test
    void login_invalidPassword_throwsException() {
        LoginRequest loginRequest = new LoginRequest("john@example.com", "wrong");

        doThrow(BadCredentialsException.class).when(authenticationManager)
                .authenticate(any());

        assertThrows(BadCredentialsException.class, () -> service.login(loginRequest));
    }

    @Test
    void login_userNotFound_throwsException() {
        LoginRequest loginRequest = new LoginRequest("unknown@example.com", "password");

        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userDetailsService.loadUserByUsername("unknown@example.com"))
                .thenReturn(mock(UserDetails.class));
        when(employeeRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.login(loginRequest));
    }

    // REGISTER
    @Test
    void register_success() {
        RegisterRequest request = RegisterRequest.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("jane@example.com")
                .phone("1234567890")
                .hireDate(LocalDate.of(2020, 1, 1))
                .birthDate(LocalDate.of(1990, 1, 1))
                .address("Address")
                .role(EmployeeRoleEnum.EMPLOYEE)
                .status(null)
                .departmentId(null)
                .positionId(null)
                .managerId(null)
                .build();


        // RegisterRequest request = new RegisterRequest(
      //          "Jane","Doe",,"", , LocalDate.of(1990,1,1),
        //        "Address", EmployeeRoleEnum.EMPLOYEE, null,null,null
       // );

        when(employeeRepository.existsByEmail(request.email())).thenReturn(false);
        when(employeeRepository.existsByPhone(request.phone())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        RegisterResponse response = service.register(request);
        assertNotNull(response);
        assertEquals(employee.getEmail(), response.email());
        verify(notificationService).notify(any(Employee.class), anyString(), anyString(), any());
    }

    @Test
    void register_emailExists_throwsException() {
        RegisterRequest request = mock(RegisterRequest.class);
        when(request.email()).thenReturn("john@example.com");
        when(employeeRepository.existsByEmail(request.email())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> service.register(request));
    }

    @Test
    void register_phoneExists_throwsException() {
        RegisterRequest request = mock(RegisterRequest.class);
        when(request.email()).thenReturn("new@example.com");
        when(request.phone()).thenReturn("123");
        when(employeeRepository.existsByEmail(request.email())).thenReturn(false);
        when(employeeRepository.existsByPhone(request.phone())).thenReturn(true);

        assertThrows(PhoneNumberAlreadyExistsException.class, () -> service.register(request));
    }

    // CHANGE PASSWORD
    @Test
    void changePassword_success() {
        when(employeeRepository.findByEmail(employee.getEmail())).thenReturn(Optional.of(employee));
        when(passwordEncoder.matches("oldPass", employee.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("newEncodedPass");
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        service.changePassword(employee.getEmail(), "oldPass", "newPass");
        assertFalse(employee.isMustChangePassword());
        assertEquals("newEncodedPass", employee.getPassword());
    }

    @Test
    void changePassword_incorrectOldPassword_throwsException() {
        when(employeeRepository.findByEmail(employee.getEmail())).thenReturn(Optional.of(employee));
        when(passwordEncoder.matches("wrongOld", employee.getPassword())).thenReturn(false);

        assertThrows(IncorrectPasswordException.class, () -> service.changePassword(employee.getEmail(), "wrongOld", "newPass"));
    }

    @Test
    void changePassword_employeeNotFound_throwsException() {
        when(employeeRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.changePassword("unknown@example.com", "old", "new"));
    }

    // TOKEN VALIDATION
    @Test
    void isTokenValid_success() {
        String token = "jwtToken";
        when(jwtUtil.extractUsername(token)).thenReturn(employee.getEmail());
        when(userDetailsService.loadUserByUsername(employee.getEmail())).thenReturn(mock(UserDetails.class));
        when(jwtUtil.validateToken(eq(token), any(UserDetails.class))).thenReturn(true);

        assertTrue(service.isTokenValid(token));
    }

    @Test
    void isTokenValid_invalidToken_returnsFalse() {
        String token = "invalidToken";
        when(jwtUtil.extractUsername(token)).thenThrow(new RuntimeException());

        assertFalse(service.isTokenValid(token));
    }
}
