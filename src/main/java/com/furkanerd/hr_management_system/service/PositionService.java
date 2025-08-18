package com.furkanerd.hr_management_system.service;

import com.furkanerd.hr_management_system.model.dto.request.position.PositionCreateRequest;
import com.furkanerd.hr_management_system.model.dto.request.position.PositionUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.response.position.ListPositionResponse;
import com.furkanerd.hr_management_system.model.dto.response.position.PositionDetailResponse;
import com.furkanerd.hr_management_system.model.entity.Position;

import java.util.List;
import java.util.UUID;

public interface PositionService {

    List<ListPositionResponse> listAllPositions();

    PositionDetailResponse getPositionById(UUID id);

    PositionDetailResponse createPosition(PositionCreateRequest createRequest);

    PositionDetailResponse updatePosition(UUID positionID, PositionUpdateRequest updateRequest);

    void deletePosition(UUID positionID);

    Position getPositionEntityById(UUID positionId);

}
