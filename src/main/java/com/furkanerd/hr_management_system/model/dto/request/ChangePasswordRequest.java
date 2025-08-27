package com.furkanerd.hr_management_system.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

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
     @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
             message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, one special character (@#$%^&+=!) and no whitespace.")
     String newPassword
){}
