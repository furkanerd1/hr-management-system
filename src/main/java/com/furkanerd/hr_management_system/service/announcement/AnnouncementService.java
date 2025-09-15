package com.furkanerd.hr_management_system.service.announcement;

import com.furkanerd.hr_management_system.model.dto.request.announcement.AnnouncementCreateRequest;
import com.furkanerd.hr_management_system.model.dto.response.PaginatedResponse;
import com.furkanerd.hr_management_system.model.dto.response.announcement.AnnouncementResponse;

public interface AnnouncementService {

    AnnouncementResponse createAnnouncement(AnnouncementCreateRequest createRequest,String hrEmail);

    PaginatedResponse<AnnouncementResponse> getAllAnnouncements(int page, int size,String sortBy, String sortDirection);
}
