package com.employee.api.service.impl;

import com.employee.api.dto.DepartmentDto;
import com.employee.api.dto.PageResponse;
import com.employee.api.entity.Department;
import com.employee.api.exception.ResourceNotFoundException;
import com.employee.api.mapper.DepartmentMapper;
import com.employee.api.repository.DepartmentRepository;
import com.employee.api.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Supplier;

import static com.employee.api.service.common.CommonService.getNotFoundExceptionSupplier;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자 자동 생성
@Transactional // 서비스 계층에서 트랜잭션 관리, 트랜잭션: 서비스 메서드가 실행될 때 트랜잭션이 시작되고, 메서드가 정상적으로 완료되면 트랜잭션이 커밋되고, 예외가 발생하면 롤백됩니다.
// 영속성 컨텍스트가 있어서 save 메서드 호출하지 않아도 됨
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
        return departmentRepository.findById(departmentId) // Oprional<Department>임
                //.map(department -> DepartmentMapper.mapToDepartmentDto(department)); // Entity => DTO 변환
                .map(DepartmentMapper::mapToDepartmentDto) // Entity => DTO 변환. Optional<DepartmentDto> 반환
                .orElseThrow(getNotFoundExceptionSupplier(
                        "Department is not exists with a given id: ", departmentId));// Optional이 비어있을 때 예외 발생
    }


    @Override
    public List<DepartmentDto> getAllDepartments() {
        List<Department> departments = departmentRepository.findAll();
        // List<Department> => Stream<Department>
        return departments.stream() // Stream<Department> (엔티티를 디티오로 바꾸기 위해 stream 사용)
                .map(DepartmentMapper::mapToDepartmentDto) // Stream<DepartmentDto> (엔티티 => DTO 변환)
                // Stream<DepartmentDto> => List<DepartmentDto>
                .toList(); // List<DepartmentDto> (Stream => List)
                //.map((department) -> DepartmentMapper.mapToDepartmentDto(department))
    }

    /*
   pageNo - 페이지 번호 (0부터 시작),
   pageSize - 페이지당 데이터 수
   sortBy - 정렬 기준 컬럼: `id`, `departmentName`, `departmentDescription`
   sortDir - 정렬 방향: `asc` / `desc`
    */
    @Override
    public PageResponse<DepartmentDto> getDepartmentsPage(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort); // Pageable 객체 생성 (페이지 번호, 페이지 크기, 정렬 정보 포함)
        Page<Department> page = departmentRepository.findAll(pageable);
        // Page<Department> (엔티티 페이지), findAll(Pageable pageable) 메서드는 페이지네이션과 정렬을 지원하는 메서드로,
        // Pageable 객체를 인자로 받아 해당 페이지에 대한 데이터를 반환합니다.

        // List<Department> => Stream<Department> => Stream<DepartmentDto> => List<DepartmentDto>
        List<DepartmentDto> content = page.getContent()
                .stream()
                //.map(dept -> DepartmentMapper.mapToDepartmentDto(dept))
                .map(DepartmentMapper::mapToDepartmentDto)
                .toList();

        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast() // isLast()는 현재 페이지가 마지막 페이지인지 여부를 반환하는 메서드입니다. true이면 마지막 페이지, false이면 마지막 페이지가 아님
        );
    }

    @Override
    public DepartmentDto updateDepartment(Long departmentId, DepartmentDto updatedDepartment) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(getNotFoundExceptionSupplier("Department is not exists with a given id:", departmentId)
                );
        // setter 호출해서 변경
        department.setDepartmentName(updatedDepartment.getDepartmentName());
        department.setDepartmentDescription(updatedDepartment.getDepartmentDescription());

        // Department savedDepartment = departmentRepository.save(department);

        // Entity => DTO 변환
        return DepartmentMapper.mapToDepartmentDto(department);
    }

    @Override
    public void deleteDepartment(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(getNotFoundExceptionSupplier("Department is not exists with a given id:", departmentId)
                );
        departmentRepository.delete(department);
    }
}
