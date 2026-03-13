package com.employee.api.service;

import com.employee.api.dto.DepartmentDto;
import com.employee.api.entity.Department;
import com.employee.api.exception.ResourceNotFoundException;
import com.employee.api.mapper.DepartmentMapper;
import com.employee.api.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@RequiredArgsConstructor // final 필드에 대한 생성자 자동 생성
@Transactional// 서비스 계층에서 트랜잭션 관리, 트랜잭션: 서비스 메서드가 실행될 때 트랜잭션이 시작되고, 메서드가 정상적으로 완료되면 트랜잭션이 커밋되고, 예외가 발생하면 롤백됩니다.
public class DepartmentServiceImpl  implements DepartmentService {
    private final DepartmentRepository departmentRepository;

    @Override
    public DepartmentDto createDepartment(DepartmentDto departmentDto) {
        // DTO => Entity 변환
        Department department = DepartmentMapper.mapToDepartment(departmentDto);
        // 등록 처리
        Department savedDepartment = departmentRepository.save(department);
        // 등록된 Entity => DTO 변환 후 반환
        return DepartmentMapper.mapToDepartmentDto(savedDepartment);
    }

    @Transactional(readOnly = true) // 조회 전용 메서드에 대해 트랜잭션을 읽기 전용으로 설정하여 성능 최적화
    @Override
    public DepartmentDto getDepartmentById(Long departmentId) {
        /*
         Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Department is not exists with a given id: " + departmentId)
        );
        Entity -> DTO 변환
        Dto -> Entity 변환 이유 : 보안성 (내부 데이터 구조 노출 방지), 유연성(엔티티 구조 변경 시 API에 영향 최소화),
        성능 최적화(필요한 데이터만 포함), 명확한 계약(API 사용자에게 명확한 데이터 구조 제공)
        return DepartmentMapper.mapToDepartmentDto(department);
        */
        departmentRepository.findById(departmentId) // Oprional<Department>임
                //.map(department -> DepartmentMapper.mapToDepartmentDto(department)); // Entity => DTO 변환
                .map(DepartmentMapper::mapToDepartmentDto) // Entity => DTO 변환. Optional<DepartmentDto> 반환
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Department is not exists with a given id: " + departmentId,
                        HttpStatus.NOT_FOUND));// Optional이 비어있을 때 예외 발생
        return ;
    }

    @Override
    public List<DepartmentDto> getAllDepartments() {
        return List.of();
    }

    @Override
    public DepartmentDto updateDepartment(Long departmentId, DepartmentDto updatedDepartment) {
        return null;
    }

    @Override
    public void deleteDepartment(Long departmentId) {

    }
}
