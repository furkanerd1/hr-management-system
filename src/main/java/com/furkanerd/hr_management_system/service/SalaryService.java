package com.furkanerd.hr_management_system.service;

import com.furkanerd.hr_management_system.model.dto.request.salary.SalaryCreateRequest;
import com.furkanerd.hr_management_system.model.dto.response.salary.ListSalaryResponse;
import com.furkanerd.hr_management_system.model.dto.response.salary.SalaryDetailResponse;


import java.util.List;

public interface SalaryService {

    List<ListSalaryResponse> listAllSalaries();

    SalaryDetailResponse createSalary(SalaryCreateRequest  createRequest);

    List<ListSalaryResponse> showEmployeeSalaryHistory(String employeeEmail);

}
