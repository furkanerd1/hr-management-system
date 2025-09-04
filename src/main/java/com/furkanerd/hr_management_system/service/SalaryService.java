package com.furkanerd.hr_management_system.service;

import com.furkanerd.hr_management_system.model.dto.request.salary.SalaryCreateRequest;
import com.furkanerd.hr_management_system.model.dto.request.salary.SalaryFilterRequest;
import com.furkanerd.hr_management_system.model.dto.response.PaginatedResponse;
import com.furkanerd.hr_management_system.model.dto.response.salary.ListSalaryResponse;
import com.furkanerd.hr_management_system.model.dto.response.salary.SalaryDetailResponse;


import java.util.UUID;

public interface SalaryService {

    PaginatedResponse<ListSalaryResponse> listAllSalaries(int page,int size,String sortBy,String sortDirection,SalaryFilterRequest filterRequest);

    SalaryDetailResponse getSalaryById(UUID id);

    SalaryDetailResponse createSalary(SalaryCreateRequest  createRequest);

    PaginatedResponse<ListSalaryResponse> showEmployeeSalaryHistory(String employeeEmail,int page,int size,String sortBy,String sortDirection,SalaryFilterRequest filterRequest);

    void deleteSalary(UUID id);

    PaginatedResponse<ListSalaryResponse> getEmployeeSalaryHistory(UUID employeeId, int page, int size, String sortBy, String sortDirection, SalaryFilterRequest filterRequest);
}
