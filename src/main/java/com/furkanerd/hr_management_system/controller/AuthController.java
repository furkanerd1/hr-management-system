package com.furkanerd.hr_management_system.controller;

import com.furkanerd.hr_management_system.model.dto.request.LoginRequest;
import com.furkanerd.hr_management_system.model.dto.request.RegisterRequest;
import com.furkanerd.hr_management_system.model.dto.response.LoginResponse;
import com.furkanerd.hr_management_system.model.dto.response.MessageResponse;
import com.furkanerd.hr_management_system.model.dto.response.RegisterResponse;
import com.furkanerd.hr_management_system.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.furkanerd.hr_management_system.config.ApiPaths.*;

@RestController
@RequestMapping(AUTH)
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest)  {
         LoginResponse loginResponse = authService.login(loginRequest);
         return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        RegisterResponse response = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<MessageResponse> changePassword(@RequestBody Map<String,String> passwords){
        String email = passwords.get("email");
        String oldPassword = passwords.get("oldPassword");
        String newPassword = passwords.get("newPassword");

        authService.changePassword(email,oldPassword,newPassword);
        return ResponseEntity.ok(new MessageResponse("Password changed successfully!"));

    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(){
        return ResponseEntity.ok(new MessageResponse("Logout successfully!"));
    }
}
