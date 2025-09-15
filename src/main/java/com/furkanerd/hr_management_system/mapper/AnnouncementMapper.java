package com.furkanerd.hr_management_system.mapper;

import com.furkanerd.hr_management_system.model.dto.response.announcement.AnnouncementResponse;
import com.furkanerd.hr_management_system.model.entity.Announcement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AnnouncementMapper {

    @Mapping(target = "createdBy",expression = "java(getFullName(announcement))")
    AnnouncementResponse toAnnouncementResponse(Announcement announcement);

    List<AnnouncementResponse> toAnnouncementResponseList(List<Announcement> announcements);

    default String getFullName(Announcement announcement) {
        return announcement.getCreatedBy().getFirstName() + " " + announcement.getCreatedBy().getLastName();
    }
}
