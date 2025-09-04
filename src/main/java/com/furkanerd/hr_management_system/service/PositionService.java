package com.furkanerd.hr_management_system.service;

import com.furkanerd.hr_management_system.model.dto.request.position.PositionCreateRequest;
import com.furkanerd.hr_management_system.model.dto.request.position.PositionFilterRequest;
import com.furkanerd.hr_management_system.model.dto.request.position.PositionUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.response.PaginatedResponse;
import com.furkanerd.hr_management_system.model.dto.response.position.ListPositionResponse;
import com.furkanerd.hr_management_system.model.dto.response.position.PositionDetailResponse;
import com.furkanerd.hr_management_system.model.entity.Position;

import java.util.UUID;

public interface PositionService {

    PaginatedResponse<ListPositionResponse> listAllPositions(int page ,int size,String sortBy,String sortDirection, PositionFilterRequest filterRequest);

    PositionDetailResponse getPositionById(UUID id);

    PositionDetailResponse createPosition(PositionCreateRequest createRequest);

    PositionDetailResponse updatePosition(UUID positionID, PositionUpdateRequest updateRequest);

    void deletePosition(UUID positionID);

    Position getPositionEntityById(UUID positionId);

}
