package com.est.tablelink.global.error;

import com.est.tablelink.global.common.ApiResponse;
import com.est.tablelink.global.common.ErrorResponse;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.NOT_VALID_ERROR,
                e.getBindingResult());
        ApiResponse<ErrorResponse> apiResponse = ApiResponse.<ErrorResponse>builder()
                .result(errorResponse)
                .resultCode(HttpStatus.BAD_REQUEST.value())
                .resultMsg("유효하지 않은 입력입니다.")
                .build();
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleIllegalArgumentException(
            IllegalArgumentException e) {
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.EMAIL_ALREADY_EXISTS,
                e.getMessage());
        ApiResponse<ErrorResponse> apiResponse = ApiResponse.<ErrorResponse>builder()
                .result(errorResponse)
                .resultCode(HttpStatus.CONFLICT.value())
                .resultMsg("이미 존재하는 이메일입니다.")
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiResponse);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleNoSuchElementException(
            NoSuchElementException e) {
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.NOT_FOUND_ERROR);
        ApiResponse<ErrorResponse> apiResponse = ApiResponse.<ErrorResponse>builder()
                .result(errorResponse)
                .resultCode(HttpStatus.NOT_FOUND.value())
                .resultMsg("요청한 데이터가 존재하지 않습니다.")
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }
}
