package com.furkanerd.hr_management_system.model.dto.request.announcement;

import com.furkanerd.hr_management_system.model.enums.AnnouncementType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AnnouncementCreateRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 100, message = "Title cannot exceed 100 characters")
        String title,

        @NotBlank(message = "Content is required")
        @Size(max = 2000, message = "Content cannot exceed 2000 characters")
        String content,

        @NotNull(message = "Announcement type is required")
        AnnouncementType type
){}
