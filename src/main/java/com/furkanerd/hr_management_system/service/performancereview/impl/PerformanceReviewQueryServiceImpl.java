package com.furkanerd.hr_management_system.service.performancereview.impl;

import com.furkanerd.hr_management_system.constants.SortFieldConstants;
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
import com.furkanerd.hr_management_system.service.performancereview.PerformanceReviewQueryService;
import com.furkanerd.hr_management_system.specification.PerformanceReviewSpecification;
import com.furkanerd.hr_management_system.util.PaginationUtils;
import com.furkanerd.hr_management_system.util.SortFieldValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class PerformanceReviewQueryServiceImpl implements PerformanceReviewQueryService {

    private final PerformanceReviewRepository performanceReviewRepository;
    private final EmployeeRepository employeeRepository;
    private final PerformanceReviewMapper performanceReviewMapper;

    public PerformanceReviewQueryServiceImpl(PerformanceReviewRepository performanceReviewRepository, EmployeeRepository employeeRepository, PerformanceReviewMapper performanceReviewMapper) {
        this.performanceReviewRepository = performanceReviewRepository;
        this.employeeRepository = employeeRepository;
        this.performanceReviewMapper = performanceReviewMapper;
    }

    @Override
    public PaginatedResponse<ListPerformanceReviewResponse> listAllPerformanceReviews(int page, int size, String sortBy, String sortDirection, PerformanceReviewFilterRequest filterRequest) {
        String validatedSortBy = SortFieldValidator.validate(SortFieldConstants.PERFORMANCE_REVIEW_SORT_FIELD, sortBy);
        Pageable pageable = PaginationUtils.buildPageable(page, size, validatedSortBy, sortDirection);

        Specification<PerformanceReview> specification = PerformanceReviewSpecification.withFilters(filterRequest);

        Page<PerformanceReview> performanceReviewPage = performanceReviewRepository.findAll(specification, pageable);
        List<ListPerformanceReviewResponse> responseList = performanceReviewMapper.performanceReviewsToListPerformanceReviewListResponse(performanceReviewPage.getContent());

        return PaginatedResponse.of(responseList, performanceReviewPage.getTotalElements(), page, size);
    }

    @Override
    public PerformanceReviewDetailResponse getPerformanceReview(UUID id) {

        PerformanceReview performanceReview = performanceReviewRepository.findById(id).orElseThrow(() -> new PerformanceReviewNotFoundException("performance review not found with id :" + id));

        return performanceReviewMapper.performanceReviewToPerformanceReviewDetailResponse(performanceReview);
    }

    @Override
    public PaginatedResponse<ListPerformanceReviewResponse> getMyPerformanceReviews(String email, int page, int size, String sortBy, String sortDirection, PerformanceReviewFilterRequest filterRequest) {
        String validatedSortBy = SortFieldValidator.validate(SortFieldConstants.PERFORMANCE_REVIEW_SORT_FIELD, sortBy);
        Pageable pageable = PaginationUtils.buildPageable(page, size, validatedSortBy, sortDirection);

        Specification<PerformanceReview> baseSpec = PerformanceReviewSpecification.withFilters(filterRequest);

        Specification<PerformanceReview> specification = (baseSpec != null) ? baseSpec.and((root, query, cb) -> cb.equal(root.get("employee").get("email"), email)) : (root, query, cb) -> cb.equal(root.get("employee").get("email"), email);

        Page<PerformanceReview> performanceReviewPage = performanceReviewRepository.findAll(specification, pageable);
        List<ListPerformanceReviewResponse> responseList = performanceReviewMapper.performanceReviewsToListPerformanceReviewListResponse(performanceReviewPage.getContent());

        return PaginatedResponse.of(responseList, performanceReviewPage.getTotalElements(), page, size);
    }

    @Override
    public PaginatedResponse<ListPerformanceReviewResponse> getPerformanceReviewsByEmployee(UUID employeeId, int page, int size, String sortBy, String sortDirection, PerformanceReviewFilterRequest filterRequest) {
        boolean exists = employeeRepository.existsById(employeeId);
        if (!exists) {
            throw new EmployeeNotFoundException(employeeId);
        }

        String validatedSortBy = SortFieldValidator.validate(SortFieldConstants.PERFORMANCE_REVIEW_SORT_FIELD, sortBy);
        Pageable pageable = PaginationUtils.buildPageable(page, size, validatedSortBy, sortDirection);

        Specification<PerformanceReview> baseSpecification = PerformanceReviewSpecification.withFilters(filterRequest);
        Specification<PerformanceReview> specification = (baseSpecification != null) ? baseSpecification.and((root, query, cb) -> cb.equal(root.get("employee").get("id"), employeeId)) : (root, query, cb) -> cb.equal(root.get("employee").get("id"), employeeId);


        Page<PerformanceReview> reviews = performanceReviewRepository.findAll(specification, pageable);
        List<ListPerformanceReviewResponse> responseList = performanceReviewMapper.performanceReviewsToListPerformanceReviewListResponse(reviews.getContent());

        return PaginatedResponse.of(responseList, reviews.getTotalElements(), page, size);
    }
}
