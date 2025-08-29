package com.furkanerd.hr_management_system.controller;

import com.furkanerd.hr_management_system.model.dto.request.position.PositionCreateRequest;
import com.furkanerd.hr_management_system.model.dto.request.position.PositionUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.response.ApiResponse;
import com.furkanerd.hr_management_system.model.dto.response.position.ListPositionResponse;
import com.furkanerd.hr_management_system.model.dto.response.position.PositionDetailResponse;
import com.furkanerd.hr_management_system.service.PositionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.furkanerd.hr_management_system.config.ApiPaths.*;

@RestController
@RequestMapping(POSITIONS)
@Tag(name = "Position", description = "Position management API")
public class PositionController {

    private final PositionService positionService;

    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }

    @Operation(
            summary = "Get all positions",
            description = "Retrieves a list of all positions. Accessible by employees and all higher-level roles."
    )
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
    // TODO: Convert to PaginatedResponse when pagination is implemented
    public  ResponseEntity<ApiResponse<List<ListPositionResponse>>> getAllPositions() {
        List<ListPositionResponse> positions = positionService.listAllPositions();
        return ResponseEntity.ok(ApiResponse.success("Positions retrieved successfully", positions));
    }

    @Operation(
            summary = "Get a position by ID",
            description = "Retrieves a specific position's details using its unique ID. Accessible by employees and all higher-level roles."
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
    public ResponseEntity<ApiResponse<PositionDetailResponse>>  getPositionById(@PathVariable("id") UUID positionId) {
        PositionDetailResponse position = positionService.getPositionById(positionId);
        return ResponseEntity.ok(ApiResponse.success("Position retrieved successfully", position));
    }

    @Operation(
            summary = "Create a new position",
            description = "Creates a new position in the system. This action is restricted to users with the HR role."
    )
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_HR')")
    public ResponseEntity<ApiResponse<PositionDetailResponse>> createPosition(@Valid @RequestBody PositionCreateRequest positionCreateRequest){
        PositionDetailResponse created = positionService.createPosition(positionCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Position created successfully", created));
    }

    @Operation(
            summary = "Update an existing position",
            description = "Updates the details of an existing position using its unique ID. This action is restricted to users with the HR role."
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_HR')")
    public ResponseEntity<ApiResponse<PositionDetailResponse>> updatePosition(
            @PathVariable("id") UUID positionId,
            @Valid @RequestBody PositionUpdateRequest positionUpdateRequest
    ){
        PositionDetailResponse updated = positionService.updatePosition(positionId, positionUpdateRequest);
        return ResponseEntity.ok(ApiResponse.success("Position updated successfully", updated));
    }

    @Operation(
            summary = "Delete a position",
            description = "Deletes a position from the system using its unique ID. This action is irreversible and restricted to users with the HR role."
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_HR')")
    public ResponseEntity<ApiResponse<Void>> deletePosition(@PathVariable("id") UUID positionId) {
        positionService.deletePosition(positionId);
        return ResponseEntity.ok(ApiResponse.success("Position deleted successfully"));
    }
}
