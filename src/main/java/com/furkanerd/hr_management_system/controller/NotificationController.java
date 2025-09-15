package com.furkanerd.hr_management_system.controller;

import com.furkanerd.hr_management_system.constants.ApiPaths;
import com.furkanerd.hr_management_system.model.dto.response.ApiResponse;
import com.furkanerd.hr_management_system.model.dto.response.PaginatedResponse;
import com.furkanerd.hr_management_system.model.dto.response.notification.NotificationResponse;
import com.furkanerd.hr_management_system.service.notification.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.NOTIFICATIONS)
@Tag(name = "Notifications", description = "Employee notifications management API")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Operation(
            summary = "Get authenticated user's notifications",
            description = "Retrieves a list of all notifications for the authenticated user. Accessible to all employees."
    )
    @GetMapping("/my-notifications")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PaginatedResponse<NotificationResponse>>> getMyNotifications(
            @AuthenticationPrincipal UserDetails currentUser,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        PaginatedResponse<NotificationResponse> notifications = notificationService
                .getMyNotifications(currentUser.getUsername(), page, size, sortBy, sortDirection);
        return ResponseEntity.ok(ApiResponse.success("Notifications retrieved successfully", notifications));
    }

    @Operation(
            summary = "Mark notification as read",
            description = "Marks a specific notification as read for the authenticated user."
    )
    @PatchMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(@PathVariable UUID id, @AuthenticationPrincipal UserDetails currentUser) {
        NotificationResponse notification = notificationService.markAsRead(id, currentUser.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read", notification));
    }

    @Operation(
            summary = "Mark all notifications as read",
            description = "Marks all unread notifications as read for the authenticated user."
    )
    @PatchMapping("/read-all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(@AuthenticationPrincipal UserDetails currentUser) {
        notificationService.markAllAsRead(currentUser.getUsername());
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read"));
    }

    @Operation(
            summary = "Delete a notification",
            description = "Deletes a specific notification for the authenticated user."
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(@PathVariable UUID id, @AuthenticationPrincipal UserDetails currentUser){
        notificationService.deleteNotification(id, currentUser.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Notification deleted successfully"));
    }
}
