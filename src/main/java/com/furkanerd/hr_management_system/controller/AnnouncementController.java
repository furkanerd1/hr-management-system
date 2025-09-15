package com.furkanerd.hr_management_system.controller;


import com.furkanerd.hr_management_system.constants.ApiPaths;
import com.furkanerd.hr_management_system.model.dto.request.announcement.AnnouncementCreateRequest;
import com.furkanerd.hr_management_system.model.dto.response.ApiResponse;
import com.furkanerd.hr_management_system.model.dto.response.PaginatedResponse;
import com.furkanerd.hr_management_system.model.dto.response.announcement.AnnouncementResponse;
import com.furkanerd.hr_management_system.service.announcement.AnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPaths.ANNOUNCEMENTS)
@Tag(name = "Announcements", description = "Company announcements management API")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @Operation(
            summary = "Create a new announcement",
            description = "Creates a new company announcement. Only HR users can perform this action."
    )
    @PostMapping
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> createAnnouncement(
            @Valid @RequestBody AnnouncementCreateRequest createRequest,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        AnnouncementResponse response = announcementService.createAnnouncement(createRequest, currentUser.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Announcement created successfully", response));
    }

    @Operation(
            summary = "Get all announcements",
            description = "Retrieves a list of all company announcements. Accessible to all employees."
    )
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PaginatedResponse<AnnouncementResponse>>> getAllAnnouncements(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection
    ){
        PaginatedResponse<AnnouncementResponse> announcements = announcementService.getAllAnnouncements(page,size,sortBy,sortDirection);
        return ResponseEntity.ok(ApiResponse.success("Announcements retrieved successfully", announcements));
    }
}
