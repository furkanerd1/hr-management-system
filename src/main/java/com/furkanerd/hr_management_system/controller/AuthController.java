package com.furkanerd.hr_management_system.controller;

import com.furkanerd.hr_management_system.model.dto.request.ChangePasswordRequest;
import com.furkanerd.hr_management_system.model.dto.request.LoginRequest;
import com.furkanerd.hr_management_system.model.dto.request.RegisterRequest;
import com.furkanerd.hr_management_system.model.dto.response.LoginResponse;
import com.furkanerd.hr_management_system.model.dto.response.MessageResponse;
import com.furkanerd.hr_management_system.model.dto.response.RegisterResponse;
import com.furkanerd.hr_management_system.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import static com.furkanerd.hr_management_system.config.ApiPaths.*;

@RestController
@RequestMapping(AUTH)
@Tag(name = "Auth Management", description = "Authentication and authorization operations")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Login an existing user",
            description = "Authenticates a user and returns a JWT token for subsequent requests.")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest)  {
         LoginResponse loginResponse = authService.login(loginRequest);
         return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/register")
    @PreAuthorize("hasAuthority('ROLE_HR')")
    @Operation(summary = "Register a new HR user", description = "Creates a new user with HR role. Requires a valid JWT token with HR authority.",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        RegisterResponse response = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change a user's password",
            description = "Allows a user to change their password using their old and new password. This method is public")
    public ResponseEntity<MessageResponse> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request.email(),request.oldPassword(),request.newPassword());
        return ResponseEntity.ok(new MessageResponse("Password changed successfully!"));

    }

    @PostMapping("/logout")
    @Operation(summary = "Logout the current user",
            description = "Invalidates the current session token.")
    public ResponseEntity<MessageResponse> logout(){
        return ResponseEntity.ok(new MessageResponse("Logout successfully!"));
    }
}
