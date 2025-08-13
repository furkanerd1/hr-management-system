package com.furkanerd.hr_management_system.controller;

import com.furkanerd.hr_management_system.model.dto.request.position.PositionCreateRequest;
import com.furkanerd.hr_management_system.model.dto.request.position.PositionUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.response.position.ListPositionResponse;
import com.furkanerd.hr_management_system.model.dto.response.position.PositionDetailResponse;
import com.furkanerd.hr_management_system.service.PositionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.furkanerd.hr_management_system.config.ApiPaths.*;

@RestController
@RequestMapping(POSITIONS)
public class PositionController {

    private final PositionService positionService;

    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }


    @GetMapping
    public ResponseEntity<List<ListPositionResponse>> getAllPositions() {
        return ResponseEntity.ok(positionService.listAllPositions());
    }

    @GetMapping(POSITIONS_BY_ID)
    public ResponseEntity<PositionDetailResponse> getPositionById(@PathVariable("id") UUID positionId) {
        return ResponseEntity.ok(positionService.getPositionById(positionId));
    }

    @PostMapping
    public ResponseEntity<PositionDetailResponse> createPosition(
            @Valid @RequestBody PositionCreateRequest positionCreateRequest
            ){
        return ResponseEntity.ok(positionService.createPosition(positionCreateRequest));
    }

    @PutMapping(POSITIONS_BY_ID)
    public ResponseEntity<PositionDetailResponse> updatePosition(
            @PathVariable("id") UUID positionId,
            @Valid @RequestBody PositionUpdateRequest positionUpdateRequest
    ){
        return  ResponseEntity.ok(positionService.updatePosition(positionId, positionUpdateRequest));
    }

    @DeleteMapping(POSITIONS_BY_ID)
    public ResponseEntity<Void> deletePosition(@PathVariable("id") UUID positionId) {
        positionService.deletePosition(positionId);
        return ResponseEntity.ok().build();
    }
}
