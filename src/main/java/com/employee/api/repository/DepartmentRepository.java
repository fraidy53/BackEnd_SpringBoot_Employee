package com.employee.api.repository;

import com.employee.api.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    // 1. 부서 이름으로 정확히 일치하는 부서 찾기
    Optional<Department> findByDepartmentName(String departmentName);

    // 2. 부서 이름에 특정 단어가 포함된 부서 목록 조회
    List<Department> findByDepartmentNameContaining(String keyword);

    // 3. 부서 설명이 비어있지 않은 부서들만 조회
    List<Department> findByDepartmentDescriptionIsNotNull();
}