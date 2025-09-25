package com.furkanerd.hr_management_system.model.dto.response.announcement;

import com.furkanerd.hr_management_system.model.enums.AnnouncementType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record AnnouncementResponse(
        UUID id,
        String title,
        String content,
        AnnouncementType type,
        String createdBy,
        LocalDateTime createdAt
){}
