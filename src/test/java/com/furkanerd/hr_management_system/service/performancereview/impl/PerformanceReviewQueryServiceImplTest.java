package com.furkanerd.hr_management_system.service.performancereview.impl;

import com.furkanerd.hr_management_system.exception.custom.EmployeeNotFoundException;
import com.furkanerd.hr_management_system.exception.custom.PerformanceReviewNotFoundException;
import com.furkanerd.hr_management_system.mapper.PerformanceReviewMapper;
import com.furkanerd.hr_management_system.model.dto.request.performancereview.PerformanceReviewFilterRequest;
import com.furkanerd.hr_management_system.model.dto.response.PaginatedResponse;
import com.furkanerd.hr_management_system.model.dto.response.performancereview.ListPerformanceReviewResponse;
import com.furkanerd.hr_management_system.model.dto.response.performancereview.PerformanceReviewDetailResponse;
import com.furkanerd.hr_management_system.model.entity.PerformanceReview;
import com.furkanerd.hr_management_system.repository.EmployeeRepository;
import com.furkanerd.hr_management_system.repository.PerformanceReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class PerformanceReviewQueryServiceImplTest {

    @Mock
    private PerformanceReviewRepository performanceReviewRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private PerformanceReviewMapper performanceReviewMapper;

    @InjectMocks
    private PerformanceReviewQueryServiceImpl performanceReviewQueryService;

    @Test
    @DisplayName("Should list all performance reviews with pagination")
    void listAllPerformanceReviews_WhenValidRequest_ShouldReturnPaginatedResponse() {
        // given
        PerformanceReviewFilterRequest filterRequest =PerformanceReviewFilterRequest.empty();
        List<PerformanceReview> reviews = Arrays.asList(
                createTestPerformanceReview(),
                createTestPerformanceReview()
        );
        Page<PerformanceReview> reviewPage = new PageImpl<>(reviews);
        List<ListPerformanceReviewResponse> responses = Arrays.asList(
                ListPerformanceReviewResponse.builder().id(UUID.randomUUID()).build(),
                ListPerformanceReviewResponse.builder().id(UUID.randomUUID()).build()
        );

        given(performanceReviewRepository.findAll(nullable(Specification.class), any(Pageable.class)))
                .willReturn(reviewPage);
        given(performanceReviewMapper.performanceReviewsToListPerformanceReviewListResponse(reviews))
                .willReturn(responses);

        // when
        PaginatedResponse<ListPerformanceReviewResponse> result = performanceReviewQueryService
                .listAllPerformanceReviews(0, 10, "reviewDate", "desc", filterRequest);

        // then
        assertThat(result.data()).hasSize(2);
        assertThat(result.total()).isEqualTo(2);
        then(performanceReviewRepository).should()
                .findAll(nullable(Specification.class), any(Pageable.class));
        then(performanceReviewMapper).should()
                .performanceReviewsToListPerformanceReviewListResponse(reviews);
    }

    @Test
    @DisplayName("Should get performance review by id")
    void getPerformanceReview_WhenReviewExists_ShouldReturnDetailResponse() {
        // given
        UUID reviewId = UUID.randomUUID();
        PerformanceReview review = createTestPerformanceReview();
        PerformanceReviewDetailResponse expectedResponse = PerformanceReviewDetailResponse.builder()
                .id(reviewId)
                .rating(4)
                .comments("Good performance")
                .build();

        given(performanceReviewRepository.findById(reviewId)).willReturn(Optional.of(review));
        given(performanceReviewMapper.performanceReviewToPerformanceReviewDetailResponse(review))
                .willReturn(expectedResponse);

        // when
        PerformanceReviewDetailResponse result = performanceReviewQueryService.getPerformanceReview(reviewId);

        // then
        assertThat(result).isEqualTo(expectedResponse);
        then(performanceReviewRepository).should().findById(reviewId);
        then(performanceReviewMapper).should()
                .performanceReviewToPerformanceReviewDetailResponse(review);
    }

    @Test
    @DisplayName("Should throw exception when performance review not found")
    void getPerformanceReview_WhenReviewNotFound_ShouldThrowException() {
        // given
        UUID reviewId = UUID.randomUUID();
        given(performanceReviewRepository.findById(reviewId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> performanceReviewQueryService.getPerformanceReview(reviewId))
                .isInstanceOf(PerformanceReviewNotFoundException.class);
        then(performanceReviewRepository).should().findById(reviewId);
    }

    @Test
    @DisplayName("Should get my performance reviews with pagination")
    void getMyPerformanceReviews_WhenValidEmail_ShouldReturnPaginatedResponse() {
        // given
        String email = "employee@company.com";
        PerformanceReviewFilterRequest filterRequest =PerformanceReviewFilterRequest.empty();
        List<PerformanceReview> reviews = Arrays.asList(createTestPerformanceReview());
        Page<PerformanceReview> reviewPage = new PageImpl<>(reviews);
        List<ListPerformanceReviewResponse> responses = Arrays.asList(
                ListPerformanceReviewResponse.builder().id(UUID.randomUUID()).build()
        );

        given(performanceReviewRepository.findAll(nullable(Specification.class), any(Pageable.class)))
                .willReturn(reviewPage);
        given(performanceReviewMapper.performanceReviewsToListPerformanceReviewListResponse(reviews))
                .willReturn(responses);

        // when
        PaginatedResponse<ListPerformanceReviewResponse> result = performanceReviewQueryService
                .getMyPerformanceReviews(email, 0, 10, "reviewDate", "desc", filterRequest);

        // then
        assertThat(result.data()).hasSize(1);
        assertThat(result.total()).isEqualTo(1);
        then(performanceReviewRepository).should()
                .findAll(nullable(Specification.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Should get performance reviews by employee id")
    void getPerformanceReviewsByEmployee_WhenEmployeeExists_ShouldReturnPaginatedResponse() {
        // given
        UUID employeeId = UUID.randomUUID();
        PerformanceReviewFilterRequest filterRequest =PerformanceReviewFilterRequest.empty();
        List<PerformanceReview> reviews = Arrays.asList(createTestPerformanceReview());
        Page<PerformanceReview> reviewPage = new PageImpl<>(reviews);
        List<ListPerformanceReviewResponse> responses = Arrays.asList(
                ListPerformanceReviewResponse.builder().id(UUID.randomUUID()).build()
        );

        given(employeeRepository.existsById(employeeId)).willReturn(true);
        given(performanceReviewRepository.findAll(nullable(Specification.class), any(Pageable.class)))
                .willReturn(reviewPage);
        given(performanceReviewMapper.performanceReviewsToListPerformanceReviewListResponse(reviews))
                .willReturn(responses);

        // when
        PaginatedResponse<ListPerformanceReviewResponse> result = performanceReviewQueryService
                .getPerformanceReviewsByEmployee(employeeId, 0, 10, "reviewDate", "desc", filterRequest);

        // then
        assertThat(result.data()).hasSize(1);
        then(employeeRepository).should().existsById(employeeId);
        then(performanceReviewRepository).should()
                .findAll(nullable(Specification.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Should throw exception when employee not found for reviews by employee")
    void getPerformanceReviewsByEmployee_WhenEmployeeNotFound_ShouldThrowException() {
        // given
        UUID employeeId = UUID.randomUUID();
        PerformanceReviewFilterRequest filterRequest =PerformanceReviewFilterRequest.empty();
        given(employeeRepository.existsById(employeeId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> performanceReviewQueryService
                .getPerformanceReviewsByEmployee(employeeId, 0, 10, "reviewDate", "desc", filterRequest))
                .isInstanceOf(EmployeeNotFoundException.class);
        then(employeeRepository).should().existsById(employeeId);
    }

    private PerformanceReview createTestPerformanceReview() {
        return PerformanceReview.builder()
                .id(UUID.randomUUID())
                .rating(4)
                .comments("Good performance")
                .build();
    }
}