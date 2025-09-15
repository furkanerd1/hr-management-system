package com.furkanerd.hr_management_system.service.announcement.impl;

import com.furkanerd.hr_management_system.exception.custom.EmployeeNotFoundException;
import com.furkanerd.hr_management_system.mapper.AnnouncementMapper;
import com.furkanerd.hr_management_system.model.dto.request.announcement.AnnouncementCreateRequest;
import com.furkanerd.hr_management_system.model.dto.response.PaginatedResponse;
import com.furkanerd.hr_management_system.model.dto.response.announcement.AnnouncementResponse;
import com.furkanerd.hr_management_system.model.entity.Announcement;
import com.furkanerd.hr_management_system.model.entity.Employee;
import com.furkanerd.hr_management_system.model.enums.NotificationTypeEnum;
import com.furkanerd.hr_management_system.repository.AnnouncementRepository;
import com.furkanerd.hr_management_system.repository.EmployeeRepository;
import com.furkanerd.hr_management_system.service.announcement.AnnouncementService;
import com.furkanerd.hr_management_system.service.notification.NotificationService;
import com.furkanerd.hr_management_system.util.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final EmployeeRepository employeeRepository;
    private final NotificationService notificationService;
    private final AnnouncementMapper announcementMapper;

    public AnnouncementServiceImpl(AnnouncementRepository announcementRepository, EmployeeRepository employeeRepository, NotificationService notificationService, AnnouncementMapper announcementMapper) {
        this.announcementRepository = announcementRepository;
        this.employeeRepository = employeeRepository;
        this.notificationService = notificationService;
        this.announcementMapper = announcementMapper;
    }


    @Override
    @Transactional
    public AnnouncementResponse createAnnouncement(AnnouncementCreateRequest createRequest, String hrEmail) {
        Employee hr = employeeRepository.findByEmail(hrEmail)
                .orElseThrow(() -> new EmployeeNotFoundException(hrEmail));

        Announcement announcement = Announcement.builder()
                .title(createRequest.title())
                .content(createRequest.content())
                .type(createRequest.type())
                .createdBy(hr)
                .build();

        Announcement savedAnnouncement = announcementRepository.save(announcement);

        String subject = "[" + savedAnnouncement.getType() + "] " + savedAnnouncement.getTitle();
        notificationService.notifyAllEmployeesForAnnouncement("📢 New announcement: " + subject, savedAnnouncement.getContent());

        return announcementMapper.toAnnouncementResponse(savedAnnouncement);
    }

    @Override
    public PaginatedResponse<AnnouncementResponse> getAllAnnouncements(int page, int size, String sortBy, String sortDirection) {
        Pageable pageable = PaginationUtils.buildPageable(page, size, sortBy, sortDirection);

        Page<Announcement> notificationPage = announcementRepository.findAll(pageable);
        List<AnnouncementResponse> responseList = announcementMapper.toAnnouncementResponseList(notificationPage.getContent());

        return PaginatedResponse.of(
                responseList, notificationPage.getTotalElements(), page, size
        );
    }
}
