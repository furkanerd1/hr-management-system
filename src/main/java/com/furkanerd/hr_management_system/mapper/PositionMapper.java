package com.furkanerd.hr_management_system.mapper;


import com.furkanerd.hr_management_system.model.dto.response.position.ListPositionResponse;
import com.furkanerd.hr_management_system.model.dto.response.position.PositionDetailResponse;
import com.furkanerd.hr_management_system.model.entity.Position;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PositionMapper {

    List<ListPositionResponse> positionsToListPositionResponses(List<Position> positions);

    PositionDetailResponse positionToPositionDetailResponse(Position position);
}
