package com.furkanerd.hr_management_system.service.impl;

import com.furkanerd.hr_management_system.exception.InvalidReviewDateException;
import com.furkanerd.hr_management_system.exception.PerformanceReviewNotFoundException;
import com.furkanerd.hr_management_system.exception.SelfReviewNotAllowedException;
import com.furkanerd.hr_management_system.exception.UnauthorizedActionException;
import com.furkanerd.hr_management_system.helper.EmployeeDomainService;
import com.furkanerd.hr_management_system.mapper.PerformanceReviewMapper;
import com.furkanerd.hr_management_system.model.dto.request.performancereview.PerformanceReviewCreateRequest;
import com.furkanerd.hr_management_system.model.dto.request.performancereview.PerformanceReviewFilterRequest;
import com.furkanerd.hr_management_system.model.dto.request.performancereview.PerformanceReviewUpdateRequest;
import com.furkanerd.hr_management_system.model.dto.response.PaginatedResponse;
import com.furkanerd.hr_management_system.model.dto.response.performancereview.ListPerformanceReviewResponse;
import com.furkanerd.hr_management_system.model.dto.response.performancereview.PerformanceReviewDetailResponse;
import com.furkanerd.hr_management_system.model.entity.Employee;
import com.furkanerd.hr_management_system.model.entity.PerformanceReview;
import com.furkanerd.hr_management_system.model.entity.Salary;
import com.furkanerd.hr_management_system.repository.PerformanceReviewRepository;
import com.furkanerd.hr_management_system.service.PerformanceReviewService;
import com.furkanerd.hr_management_system.specification.PerformanceReviewSpecification;
import com.furkanerd.hr_management_system.util.PaginationUtils;
import com.furkanerd.hr_management_system.util.SortFieldValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
class PerformanceReviewServiceImpl implements PerformanceReviewService {

    private final PerformanceReviewRepository performanceReviewRepository;
    private final PerformanceReviewMapper performanceReviewMapper;
    private final EmployeeDomainService  employeeDomainService;

    public PerformanceReviewServiceImpl(PerformanceReviewRepository performanceReviewRepository, PerformanceReviewMapper performanceReviewMapper,EmployeeDomainService employeeDomainService) {
        this.performanceReviewRepository = performanceReviewRepository;
        this.performanceReviewMapper = performanceReviewMapper;
        this.employeeDomainService = employeeDomainService;
    }

    @Override
    public PaginatedResponse<ListPerformanceReviewResponse> listAllPerformanceReviews(int page,int size,String sortBy,String sortDirection,PerformanceReviewFilterRequest filterRequest) {
        String validatedSortBy = SortFieldValidator.validate("performanceReview",sortBy);
        Pageable pageable = PaginationUtils.buildPageable(page,size,validatedSortBy,sortDirection);

        Specification<PerformanceReview> specification = PerformanceReviewSpecification.withFilters(filterRequest);

       Page<PerformanceReview> performanceReviewPage = performanceReviewRepository.findAll(specification,pageable);
       List<ListPerformanceReviewResponse>  responseList = performanceReviewMapper
               .performanceReviewsToListPerformanceReviewListResponse(performanceReviewPage.getContent());

        return PaginatedResponse.of(
                responseList,
                performanceReviewPage.getTotalElements(),
                page,
                size
        );
    }

    @Override
    public PerformanceReviewDetailResponse getPerformanceReview(UUID id) {

        PerformanceReview performanceReview = performanceReviewRepository.findById(id)
                .orElseThrow(() -> new PerformanceReviewNotFoundException("performance review not found with id :"+ id));

        return  performanceReviewMapper.performanceReviewToPerformanceReviewDetailResponse(performanceReview);
    }

    @Override
    public PaginatedResponse<ListPerformanceReviewResponse> getMyPerformanceReviews(String email,int page,int size,String sortBy,String sortDirection,PerformanceReviewFilterRequest filterRequest) {
        String validatedSortBy = SortFieldValidator.validate("performanceReview",sortBy);
        Pageable pageable = PaginationUtils.buildPageable(page,size,validatedSortBy,sortDirection);

        Specification<PerformanceReview> baseSpec = PerformanceReviewSpecification.withFilters(filterRequest);

        Specification<PerformanceReview> specification = (baseSpec != null)
                ? baseSpec.and((root, query, cb) -> cb.equal(root.get("employee").get("email"), email))
                : (root, query, cb) -> cb.equal(root.get("employee").get("email"), email);

        Page<PerformanceReview> performanceReviewPage = performanceReviewRepository.findAll(specification,pageable);
        List<ListPerformanceReviewResponse>  responseList = performanceReviewMapper
                .performanceReviewsToListPerformanceReviewListResponse(performanceReviewPage.getContent());

        return PaginatedResponse.of(
                responseList,
                performanceReviewPage.getTotalElements(),
                page,
                size
        );
    }

    @Override
    @Transactional
    public PerformanceReviewDetailResponse createPerformanceReview(PerformanceReviewCreateRequest createRequest, String email) {

        Employee employee = employeeDomainService.getEmployeeById(createRequest.employeeId());

        Employee reviewer= employeeDomainService.getEmployeeByEmail(email);

        if (employee.getId().equals(reviewer.getId())) {
            throw new SelfReviewNotAllowedException("Employee cannot review themselves");
        }

        LocalDate reviewDate = createRequest.reviewDate() != null ? createRequest.reviewDate() : LocalDate.now();
        if (reviewDate.isAfter(LocalDate.now())) {
            throw new InvalidReviewDateException("Review date cannot be in the future");
        }

        // create
        PerformanceReview performanceReview = PerformanceReview.builder()
                .employee(employee)
                .reviewer(reviewer)
                .rating(createRequest.rating())
                .comments(createRequest.comments())
                .reviewDate(reviewDate)
                .build();

        return performanceReviewMapper.performanceReviewToPerformanceReviewDetailResponse(performanceReviewRepository.save(performanceReview));
    }

    @Override
    @Transactional
    public PerformanceReviewDetailResponse updatePerformanceReview(UUID id, PerformanceReviewUpdateRequest updateRequest, String reviewerEmail) {

        PerformanceReview performanceReview = performanceReviewRepository.findById(id)
                .orElseThrow(() -> new PerformanceReviewNotFoundException("Performance review not found with id :"+ id));

        Employee reviewer = employeeDomainService.getEmployeeByEmail(reviewerEmail);


        if(!performanceReview.getReviewer().getId().equals(reviewer.getId())) {
            throw new UnauthorizedActionException("You can only update reviews you created");
        }

        performanceReview.setRating(updateRequest.rating());
        performanceReview.setComments(updateRequest.comments());

        LocalDate reviewDate = updateRequest.reviewDate() != null ? updateRequest.reviewDate() : LocalDate.now();
        if (reviewDate.isAfter(LocalDate.now())) {
            throw new InvalidReviewDateException("Review date cannot be in the future");
        }

        performanceReview.setReviewDate(reviewDate);
        return performanceReviewMapper.performanceReviewToPerformanceReviewDetailResponse(performanceReviewRepository.save(performanceReview));
    }

    @Override
    @Transactional
    public void deletePerformanceReview(UUID id, String reviewerEmail) {
        Employee reviewer =  employeeDomainService.getEmployeeByEmail(reviewerEmail);
        PerformanceReview performanceReview = performanceReviewRepository.findById(id)
                .orElseThrow(() -> new PerformanceReviewNotFoundException("Performance review not found with id :"+ id));

        if(!performanceReview.getReviewer().getId().equals(reviewer.getId())) {
            throw new UnauthorizedActionException("You can only delete reviews you created");
        }

        performanceReviewRepository.delete(performanceReview);

    }

    @Override
    public PaginatedResponse<ListPerformanceReviewResponse> getPerformanceReviewByEmployeeId(UUID employeeId, int page, int size, String sortBy, String sortDirection, PerformanceReviewFilterRequest filterRequest) {
        String validatedSortBy = SortFieldValidator.validate("performanceReview",sortBy);
        Pageable pageable = PaginationUtils.buildPageable(page, size, validatedSortBy, sortDirection);

        Specification<PerformanceReview> baseSpecification = PerformanceReviewSpecification.withFilters(filterRequest);
        Specification<PerformanceReview> specification = (baseSpecification != null)
                ? baseSpecification.and((root, query, cb) -> cb.equal(root.get("employee").get("id"), employeeId))
                : (root, query, cb) -> cb.equal(root.get("employee").get("id"), employeeId);


        Page<PerformanceReview> reviews = performanceReviewRepository.findAll(specification, pageable);
        List<ListPerformanceReviewResponse> responseList =
                performanceReviewMapper.performanceReviewsToListPerformanceReviewListResponse(reviews.getContent());

        return PaginatedResponse.of(
                responseList,
                reviews.getTotalElements(),
                page,
                size
        );
    }
}