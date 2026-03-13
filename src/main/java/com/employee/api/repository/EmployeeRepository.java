package com.employee.api.repository;

import com.employee.api.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    // 1. 이메일로 직원 찾기 (unique 제약조건 대응)
    Optional<Employee> findByEmail(String email);

    // 2. 성(lastName)이 일치하는 모든 직원 찾기
    List<Employee> findByLastName(String lastName);

    // 3. 이름 또는 성에 특정 문자열이 포함된 직원 검색 (Like 검색)
    List<Employee> findByFirstNameContainingOrLastNameContaining(String firstName, String lastName);

    // 4. 특정 부서 ID에 속한 모든 직원 조회
    List<Employee> findByDepartmentId(Long departmentId);

    // 5. [성능 최적화] 부서 정보까지 한 번에 가져오기 (Fetch Join)
    // Fetch Join을 사용하여 N+1 문제를 해결하고, 직원과 부서 정보를 한 번의 쿼리로 가져옵니다.
    // Fetcj Join : JPA에서 연관된 엔티티를 한 번의 쿼리로 함께 가져오는 방법입니다.
    // 예를 들어, Employee 엔티티가 Department 엔티티와 ManyToOne 관계에 있을 때, Employee를 조회할 때 Department 정보도 함께 가져오도록 하는 것입니다.
    // 이렇게 하면 N+1 문제를 방지할 수 있습니다.
    // N+1 문제란, 예를 들어 Employee를 조회할 때 각 Employee마다 Department를 조회하는 추가 쿼리가 발생하는 상황
    @Query("SELECT e FROM Employee e JOIN FETCH e.department")
    List<Employee> findAllWithDepartment();
}