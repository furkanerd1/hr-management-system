package com.furkanerd.hr_management_system.model.dto.response.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.furkanerd.hr_management_system.model.enums.NotificationTypeEnum;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record NotificationResponse(
        UUID id,
        String message,
        NotificationTypeEnum type,
        @JsonProperty("isRead")
        boolean isRead,
        LocalDateTime createdAt
) {
}
