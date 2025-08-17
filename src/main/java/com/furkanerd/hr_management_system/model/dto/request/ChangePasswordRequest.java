package com.furkanerd.hr_management_system.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(

     @NotBlank(message = "Email cannot be blank")
     @Email(message = "Invalid email format")
     @Schema(description = "User's email address", example = "john.doe@example.com")
     String email,

     @NotBlank(message = "Old password cannot be blank")
     @Schema(description = "The user's current password", example = "OldPassword123!")
     String oldPassword,

     @NotBlank(message = "New password cannot be blank")
     @Schema(description = "The new password to set", example = "NewPassword456!")
     String newPassword
){}
