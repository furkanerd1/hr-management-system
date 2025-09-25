package com.furkanerd.hr_management_system.service.position.impl;

import com.furkanerd.hr_management_system.exception.custom.PositionNotFoundException;
import com.furkanerd.hr_management_system.mapper.PositionMapper;
import com.furkanerd.hr_management_system.model.dto.request.position.PositionCreateRequest;
import com.furkanerd.hr_management_system.model.dto.request.position.PositionUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.response.PaginatedResponse;
import com.furkanerd.hr_management_system.model.dto.response.position.ListPositionResponse;
import com.furkanerd.hr_management_system.model.dto.response.position.PositionDetailResponse;
import com.furkanerd.hr_management_system.model.entity.Position;
import com.furkanerd.hr_management_system.repository.PositionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PositionServiceImplTest {

    @InjectMocks
    private PositionServiceImpl service;

    @Mock
    private PositionRepository positionRepository;

    @Mock
    private PositionMapper positionMapper;

    private Position position;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        position = Position.builder()
                .id(UUID.randomUUID())
                .title("Developer")
                .description("Develops software")
                .build();
    }

    // LIST ALL
    @Test
    void listAllPositions_success() {
        Page<Position> page = new PageImpl<>(List.of(position));
        when(positionRepository.findAll(nullable(Specification.class), any(Pageable.class))).thenReturn(page);
        when(positionMapper.positionsToListPositionResponses(anyList())).thenReturn(List.of(mock(ListPositionResponse.class)));

        PaginatedResponse<ListPositionResponse> response = service.listAllPositions(0, 10, "title", "asc", null);
        assertNotNull(response);
        assertEquals(1, response.data().size());
    }

    // GET BY ID
    @Test
    void getPositionById_success() {
        when(positionRepository.findById(position.getId())).thenReturn(Optional.of(position));
        when(positionMapper.positionToPositionDetailResponse(position)).thenReturn(mock(PositionDetailResponse.class));

        PositionDetailResponse response = service.getPositionById(position.getId());
        assertNotNull(response);
    }

    @Test
    void getPositionById_notFound_throwsException() {
        UUID id = UUID.randomUUID();
        when(positionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(PositionNotFoundException.class, () -> service.getPositionById(id));
    }

    // CREATE
    @Test
    void createPosition_success() {
        PositionCreateRequest createRequest = new PositionCreateRequest("Tester", "Testing software");
        Position newPosition = Position.builder().title("Tester").description("Testing software").build();

        when(positionRepository.save(any())).thenReturn(newPosition);
        when(positionMapper.positionToPositionDetailResponse(newPosition)).thenReturn(mock(PositionDetailResponse.class));

        PositionDetailResponse response = service.createPosition(createRequest);
        assertNotNull(response);
        verify(positionRepository).save(any());
    }

    // UPDATE
    @Test
    void updatePosition_success() {
        PositionUpdateRequest updateRequest = new PositionUpdateRequest("Senior Developer", "Develops software in depth");

        when(positionRepository.findById(position.getId())).thenReturn(Optional.of(position));
        when(positionRepository.save(any())).thenReturn(position);
        when(positionMapper.positionToPositionDetailResponse(any())).thenReturn(mock(PositionDetailResponse.class));

        PositionDetailResponse response = service.updatePosition(position.getId(), updateRequest);
        assertNotNull(response);
        assertEquals("Senior Developer", position.getTitle());
        assertEquals("Develops software in depth", position.getDescription());
    }

    @Test
    void updatePosition_notFound_throwsException() {
        UUID id = UUID.randomUUID();
        PositionUpdateRequest updateRequest = new PositionUpdateRequest("Title", "Desc");
        when(positionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(PositionNotFoundException.class, () -> service.updatePosition(id, updateRequest));
    }

    // DELETE
    @Test
    void deletePosition_success() {
        when(positionRepository.existsById(position.getId())).thenReturn(true);

        service.deletePosition(position.getId());
        verify(positionRepository).deleteById(position.getId());
    }

    @Test
    void deletePosition_notFound_throwsException() {
        UUID id = UUID.randomUUID();
        when(positionRepository.existsById(id)).thenReturn(false);

        assertThrows(PositionNotFoundException.class, () -> service.deletePosition(id));
    }

    // GET ENTITY BY ID
    @Test
    void getPositionEntityById_success() {
        when(positionRepository.findById(position.getId())).thenReturn(Optional.of(position));

        Position result = service.getPositionEntityById(position.getId());
        assertNotNull(result);
        assertEquals(position.getId(), result.getId());
    }

    @Test
    void getPositionEntityById_notFound_throwsException() {
        UUID id = UUID.randomUUID();
        when(positionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(PositionNotFoundException.class, () -> service.getPositionEntityById(id));
    }
}
