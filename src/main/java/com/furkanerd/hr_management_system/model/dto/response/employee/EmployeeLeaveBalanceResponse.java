package com.furkanerd.hr_management_system.model.dto.response.employee;

import java.util.UUID;

public record EmployeeLeaveBalanceResponse(
         UUID employeeId,
         int vacationBalance,
         int maternityBalance
){}
