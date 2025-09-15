package com.furkanerd.hr_management_system.mapper;

import com.furkanerd.hr_management_system.model.dto.response.notification.NotificationResponse;
import com.furkanerd.hr_management_system.model.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationMapper {

    @Mapping(target = "isRead", source = "read")
    NotificationResponse toNotificationResponse(Notification notification);

    @Mapping(target = "isRead", source = "read")
    List<NotificationResponse> notificationsToListNotificationResponse(List<Notification> notifications);
}
