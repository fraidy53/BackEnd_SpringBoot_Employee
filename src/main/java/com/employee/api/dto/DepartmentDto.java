package com.employee.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentDto {
    private Long id;

    @NotBlank(message = "departmentName은 필수 입력 항목입니다.")
    private String departmentName;

    @NotBlank(message = "departmentDescription은 필수 입력 항목입니다.")
    private String departmentDescription;
}