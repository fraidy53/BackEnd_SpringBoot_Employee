package com.employee.api.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor // 기본 생성자 추가, @Builder 사용 시 필요
@AllArgsConstructor // Builer 패턴 사용을 위해 @AllArgsConstructor 추가, @Builder 사용 시 모든 필드를 포함하는 생성자가 필요하기 때문
@Builder
public class EmployeeDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Long departmentId;
    private DepartmentDto departmentDto;

}