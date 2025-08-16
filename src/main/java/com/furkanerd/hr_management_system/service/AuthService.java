package com.furkanerd.hr_management_system.service;

import com.furkanerd.hr_management_system.model.dto.request.LoginRequest;
import com.furkanerd.hr_management_system.model.dto.request.RegisterRequest;
import com.furkanerd.hr_management_system.model.dto.response.LoginResponse;
import com.furkanerd.hr_management_system.model.dto.response.RegisterResponse;

public interface AuthService {

    LoginResponse login(LoginRequest loginRequest);

    RegisterResponse register(RegisterRequest registerRequest);

    void changePassword(String email,String oldPassword,String newPassword);

    boolean isTokenValid(String token);
}
