package com.employee.api.service.impl;

import com.employee.api.dto.EmployeeDto;
import com.employee.api.dto.PageResponse;
import com.employee.api.entity.Department;
import com.employee.api.entity.Employee;
import com.employee.api.exception.ResourceNotFoundException;
import com.employee.api.mapper.EmployeeMapper;
import com.employee.api.repository.DepartmentRepository;
import com.employee.api.repository.EmployeeRepository;
import com.employee.api.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.employee.api.service.common.CommonService.getNotFoundExceptionSupplier;

@Service
@Transactional
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public EmployeeDto createEmployee(EmployeeDto employeeDto) {
        //입력 받은 DTO => Entity
        Employee employee = EmployeeMapper.mapToEmployee(employeeDto);
        //Department 의 존재여부를 조회
        Department department = departmentRepository.findById(employeeDto.getDepartmentId())
                .orElseThrow(getNotFoundExceptionSupplier(
                        "Department is not exists with id: ",
                        employeeDto.getDepartmentId())
                );
        //Employee 와 Department 연결
        employee.setDepartment(department);
        //Employee 등록
        Employee savedEmployee = employeeRepository.save(employee);
        //DB에 등록된 Entity => DTO
        return EmployeeMapper.mapToEmployeeDto(savedEmployee);
    }

    @Transactional(readOnly = true)
    @Override
    public EmployeeDto getEmployeeById(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(
                        getNotFoundExceptionSupplier(
                                "Employee is not exists with given id : ",
                                employeeId)
                );

        return EmployeeMapper.mapToEmployeeDto(employee);
    }

    @Transactional(readOnly = true)
    @Override
    public List<EmployeeDto> getAllEmployees() {
        // N+1 문제 발생 (Employee를 조회할 때 Department도 함께 조회하는 경우)
        List<Employee> employees = employeeRepository.findAll();
        return employees.stream()
                .map(EmployeeMapper::mapToEmployeeDto) // id만 가져오는 것 (Lazy로 해서 쿼리가 1개임)
                //.map(EmployeeMapper::mapToEmployeeDepartmentDto) // id + department 정보까지 가져오는 것 (Fetch Join과 유사)
                // Lazy로 하면 Employee를 조회할 때 Department는 실제로 필요한 시점까지 조회하지 않음 (프록시 객체로 대체)
                // Eager로 하면 Employee를 조회할 때 Department도 함께 조회 (Fetch Join과 유사)
                .toList();
        //.map((employee) -> EmployeeMapper.mapToEmployeeDto(employee))
        //.collect(Collectors.toList());
    }

    @Override
    public PageResponse<EmployeeDto> getEmployeesPage(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Employee> page = employeeRepository.findAll(pageable);

        List<EmployeeDto> content = page.getContent()
                .stream()
                .map(EmployeeMapper::mapToEmployeeDto)
                .toList();

        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    @Override
    public List<EmployeeDto> getAllEmployeesDepartment() {
        // Fetch Join을 사용하여 직원과 부서 정보를 한 번의 쿼리로 가져옴
        List<Employee> employees = employeeRepository.findAllWithDepartment();
        return employees.stream()
                .map(EmployeeMapper::mapToEmployeeDepartmentDto)
                .toList();

    }

    @Override
    public EmployeeDto updateEmployee(Long employeeId, EmployeeDto updatedEmployee) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(
                        getNotFoundExceptionSupplier("Employee is not exists with given id : ", employeeId)
                );
        //setter 호출로 값을 변경
        employee.setFirstName(updatedEmployee.getFirstName());
        employee.setLastName(updatedEmployee.getLastName());
        employee.setEmail(updatedEmployee.getEmail());

        Department department = departmentRepository.findById(updatedEmployee.getDepartmentId())
                .orElseThrow(
                        getNotFoundExceptionSupplier(
                                "Department is not exists with a given id: ", updatedEmployee.getDepartmentId())
                );
        //Employee와 Department 연결
        employee.setDepartment(department);

        Employee updatedEmployeeObj = employeeRepository.save(employee);
        return EmployeeMapper.mapToEmployeeDto(updatedEmployeeObj);
    }

    @Override
    public void deleteEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(
                        getNotFoundExceptionSupplier("Employee is not exists with given id : ", employeeId)
                );
        employeeRepository.delete(employee);
    }

    @Override
    public EmployeeDto getEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email)
                //.map(entity -> EmployeeMapper.mapToEmployeeDepartmentDto(entity))
                .map(EmployeeMapper::mapToEmployeeDepartmentDto)
                .orElseThrow(
                        getNotFoundExceptionSupplier("Employee is not exists with given email : ", email)
                );
    }
}