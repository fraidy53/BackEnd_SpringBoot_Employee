package com.employee.api.exception.advice;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Data
public class ErrorObject { // 에러 응답 객체
    // 클라이언트에게 에러 정보를 JSON 형태로 반환
    private Integer statusCode;
    private String message;
    private String timestamp;

    public String getTimestamp() {
        LocalDateTime ldt = LocalDateTime.now();
        return DateTimeFormatter.ofPattern(
                "yyyy-MM-dd HH:mm:ss E a", 
                Locale.KOREA).format(ldt);
    }
}