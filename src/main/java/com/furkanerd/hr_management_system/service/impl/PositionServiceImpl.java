package com.furkanerd.hr_management_system.service.impl;

import com.furkanerd.hr_management_system.exception.PositionNotFoundException;
import com.furkanerd.hr_management_system.mapper.PositionMapper;
import com.furkanerd.hr_management_system.model.dto.request.position.PositionCreateRequest;
import com.furkanerd.hr_management_system.model.dto.request.position.PositionFilterRequest;
import com.furkanerd.hr_management_system.model.dto.request.position.PositionUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.response.PaginatedResponse;
import com.furkanerd.hr_management_system.specification.PositionSpecification;
import com.furkanerd.hr_management_system.util.PaginationUtils;
import com.furkanerd.hr_management_system.model.dto.response.position.ListPositionResponse;
import com.furkanerd.hr_management_system.model.dto.response.position.PositionDetailResponse;
import com.furkanerd.hr_management_system.model.entity.Position;
import com.furkanerd.hr_management_system.repository.PositionRepository;
import com.furkanerd.hr_management_system.service.PositionService;
import com.furkanerd.hr_management_system.util.SortFieldValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
class PositionServiceImpl implements PositionService {

    private final PositionRepository positionRepository;
    private final PositionMapper positionMapper;

    public PositionServiceImpl(PositionRepository positionRepository, PositionMapper positionMapper) {
        this.positionRepository = positionRepository;
        this.positionMapper = positionMapper;
    }

    @Override
    public PaginatedResponse<ListPositionResponse> listAllPositions(int page, int size, String sortBy, String sortDirection, PositionFilterRequest filterRequest) {
        String validatedSortBy = SortFieldValidator.validate("position",sortBy);
        Pageable pageable = PaginationUtils.buildPageable(page,size,validatedSortBy,sortDirection);

        Specification<Position> specification = PositionSpecification.withFilters(filterRequest);

        Page<Position> positionPage = positionRepository.findAll(specification,pageable);
        List<ListPositionResponse> responseList = positionMapper.positionsToListPositionResponses(positionPage.getContent());

        return PaginatedResponse.of(
                responseList,
                positionPage.getTotalElements(),
                page,
                size
        );
    }

    @Override
    public PositionDetailResponse getPositionById(UUID id) {
        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new PositionNotFoundException(id));
        return  positionMapper.positionToPositionDetailResponse(position);
    }

    @Override
    @Transactional
    public PositionDetailResponse createPosition(PositionCreateRequest createRequest) {
        Position toCreate = Position.builder()
                .title(createRequest.title())
                .description(createRequest.description())
                .build();
        return  positionMapper.positionToPositionDetailResponse(positionRepository.save(toCreate));
    }

    @Override
    @Transactional
    public PositionDetailResponse updatePosition(UUID positionID, PositionUpdateRequest updateRequest) {
        Position toUpdate =  positionRepository.findById(positionID)
                .orElseThrow(() -> new PositionNotFoundException( positionID));
        toUpdate.setTitle(updateRequest.title());
        toUpdate.setDescription(updateRequest.description());
        return   positionMapper.positionToPositionDetailResponse(positionRepository.save(toUpdate));
    }

    @Override
    @Transactional
    public void deletePosition(UUID positionID) {
        boolean exists =  positionRepository.existsById(positionID);
        if (!exists) {
            throw new PositionNotFoundException(positionID);
        }
        positionRepository.deleteById(positionID);
    }

    @Override
    public Position getPositionEntityById(UUID positionId) {
        return positionRepository.findById(positionId)
                .orElseThrow(() -> new PositionNotFoundException(positionId));
    }
}
