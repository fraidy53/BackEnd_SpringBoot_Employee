package com.employee.api.service;

import com.employee.api.dto.EmployeeDto;
import com.employee.api.dto.PageResponse;

import java.util.List;

public interface EmployeeService {
    EmployeeDto createEmployee(EmployeeDto employeeDto);

    EmployeeDto getEmployeeById(Long employeeId);

    List<EmployeeDto> getAllEmployees();

    List<EmployeeDto> getAllEmployeesDepartment();

    EmployeeDto updateEmployee(Long employeeId, EmployeeDto updatedEmployee);

    void deleteEmployee(Long employeeId);

    EmployeeDto getEmployeeByEmail(String email);

    PageResponse<EmployeeDto> getEmployeesPage(int pageNo, int pageSize, String sortBy, String sortDir);
}