package com.furkanerd.hr_management_system.service.impl;

import com.furkanerd.hr_management_system.exception.PositionNotFoundException;
import com.furkanerd.hr_management_system.mapper.PositionMapper;
import com.furkanerd.hr_management_system.model.dto.request.position.PositionCreateRequest;
import com.furkanerd.hr_management_system.model.dto.request.position.PositionUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.response.position.ListPositionResponse;
import com.furkanerd.hr_management_system.model.dto.response.position.PositionDetailResponse;
import com.furkanerd.hr_management_system.model.entity.Position;
import com.furkanerd.hr_management_system.repository.PositionRepository;
import com.furkanerd.hr_management_system.service.PositionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PositionServiceImpl implements PositionService {

    private final PositionRepository positionRepository;
    private final PositionMapper positionMapper;

    public PositionServiceImpl(PositionRepository positionRepository, PositionMapper positionMapper) {
        this.positionRepository = positionRepository;
        this.positionMapper = positionMapper;
    }

    @Override
    public List<ListPositionResponse> listAllPositions() {
        return positionMapper.positionsToListPositionResponses(positionRepository.findAll());
    }

    @Override
    public PositionDetailResponse getPositionById(UUID id) {
        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new PositionNotFoundException(id));
        return  positionMapper.positionToPositionDetailResponse(position);
    }

    @Override
    public PositionDetailResponse createPosition(PositionCreateRequest createRequest) {
        Position toCreate = Position.builder()
                .title(createRequest.title())
                .description(createRequest.description())
                .build();
        return  positionMapper.positionToPositionDetailResponse(positionRepository.save(toCreate));
    }

    @Override
    public PositionDetailResponse updatePosition(UUID positionID, PositionUpdateRequest updateRequest) {
        Position toUpdate =  positionRepository.findById(positionID)
                .orElseThrow(() -> new PositionNotFoundException( positionID));
        toUpdate.setTitle(updateRequest.title());
        toUpdate.setDescription(updateRequest.description());
        return   positionMapper.positionToPositionDetailResponse(positionRepository.save(toUpdate));
    }

    @Override
    public void deletePosition(UUID positionID) {
        boolean exists =  positionRepository.existsById(positionID);
        if (!exists) {
            throw new PositionNotFoundException(positionID);
        }
        positionRepository.deleteById(positionID);
    }
}
