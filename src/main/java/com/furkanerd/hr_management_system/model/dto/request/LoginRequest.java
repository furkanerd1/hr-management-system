package com.furkanerd.hr_management_system.model.dto.request;


import jakarta.validation.constraints.Email;

public record LoginRequest(
    @Email
    String email,
    String password
){}
