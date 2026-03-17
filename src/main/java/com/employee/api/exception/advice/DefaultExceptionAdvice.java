package com.employee.api.exception.advice;

import com.employee.api.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class DefaultExceptionAdvice { // 모든 컨트롤러에서 발생하는 예외를 처리하는 클래스

    // ResourceNotFoundException이 발생했을 때 처리하는 메서드
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorObject> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatusCode(ex.getHttpStatus().value());
        errorObject.setMessage(ex.getMessage());

        log.error(ex.getMessage(), ex);

        return new ResponseEntity<ErrorObject>(errorObject, HttpStatusCode.valueOf(ex.getHttpStatus().value()));
        // ex.getHttpStatus()는 ResourceNotFoundException에서 가져온 HTTP 상태 코드
    }

    /*
        Spring6 버전에 추가된 ProblemDetail 객체에 에러정보를 담아서 리턴하는 방법
     */
//    @ExceptionHandler(ResourceNotFoundException.class)
//    protected ProblemDetail handleException(ResourceNotFoundException e) {
//        ProblemDetail problemDetail = ProblemDetail.forStatus(e.getHttpStatus());
//        problemDetail.setTitle("Not Found");
//        problemDetail.setDetail(e.getMessage());
//        problemDetail.setProperty("errorCategory", "Generic");
//        problemDetail.setProperty("timestamp", Instant.now());
//        return problemDetail;
//    }

    //숫자타입의 값에 문자열타입의 값을 입력으로 받았을때 발생하는 오류
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<Object> handleException(HttpMessageNotReadableException e) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("message", e.getMessage());
        result.put("httpStatus", HttpStatus.BAD_REQUEST.value());

        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<ErrorObject> handleException(RuntimeException e) {
        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorObject.setMessage(e.getMessage());

        log.error(e.getMessage(), e);

        return new ResponseEntity<ErrorObject>(errorObject, HttpStatusCode.valueOf(500));
    }

    // 입력항목 검증할때 오류 발생하는 오류를 출력하는 메서드
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        log.error(ex.getMessage(), ex);

        Map<String, String> errors = new HashMap<>(); // 필드 이름과 오류 메시지를 저장할 맵
        ex.getBindingResult() // bindingResult는 검증 결과를 담고 있는 객체
                .getAllErrors() // 오류 객체 리스트를 가져옴
                .forEach((error) -> {
                    String fieldName = ((FieldError) error).getField(); // 오류가 발생한 필드 이름
                    String errorMessage = error.getDefaultMessage(); // 오류 메시지
                    errors.put(fieldName, errorMessage); // 필드 이름과 오류 메시지를 맵에 저장
                });

        ValidationErrorResponse response =
                new ValidationErrorResponse(
                        400,
                        "입력항목 검증 오류",
                        LocalDateTime.now(),
                        errors
                );
        //badRequest() 400
        return ResponseEntity.badRequest().body(response);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ValidationErrorResponse { // 입력항목 검증 오류에 대한 응답 객체
        private int status;
        private String message;
        private LocalDateTime timestamp;
        private Map<String, String> errors;
    }
} // class