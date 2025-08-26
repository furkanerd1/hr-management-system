package com.furkanerd.hr_management_system.service;

import com.furkanerd.hr_management_system.model.dto.request.salary.SalaryCreateRequest;
import com.furkanerd.hr_management_system.model.dto.response.salary.ListSalaryResponse;
import com.furkanerd.hr_management_system.model.dto.response.salary.SalaryDetailResponse;


import java.util.List;
import java.util.UUID;

public interface SalaryService {

    List<ListSalaryResponse> listAllSalaries();

    SalaryDetailResponse getSalaryById(UUID id);

    SalaryDetailResponse createSalary(SalaryCreateRequest  createRequest);

    List<ListSalaryResponse> showEmployeeSalaryHistory(String employeeEmail);

    void deleteSalary(UUID id);

    List<ListSalaryResponse> getEmployeeSalaryHistory(UUID employeeId);
}
