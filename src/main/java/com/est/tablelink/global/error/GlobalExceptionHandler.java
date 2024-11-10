package com.est.tablelink.global.error;

import com.est.tablelink.global.common.ApiResponse;
import com.est.tablelink.global.common.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final HttpServletRequest request;

    // @RequestBody 및 @RequestParam 값이 유효하지 않을 경우
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

    // 이미 존재하는 이메일 충돌
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleIllegalArgumentException(
            IllegalArgumentException e) {
        if (request.getRequestURI().equals("/api/user/signup")) {
            ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.EMAIL_ALREADY_EXISTS,
                    e.getMessage());
            ApiResponse<ErrorResponse> apiResponse = ApiResponse.<ErrorResponse>builder()
                    .result(errorResponse)
                    .resultCode(HttpStatus.CONFLICT.value())
                    .resultMsg("이미 존재하는 이메일입니다.")
                    .build();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(apiResponse);
        }
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.BAD_REQUEST_ERROR,
                e.getMessage());
        ApiResponse<ErrorResponse> apiResponse = ApiResponse.<ErrorResponse>builder()
                .result(errorResponse)
                .resultCode(HttpStatus.BAD_REQUEST.value())
                .resultMsg("잘못된 입력 입니다.")
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    // 존재하지 않는 데이터 요청
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleNoSuchElementException(
            NoSuchElementException e) {
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.NOT_FOUND_ERROR
                , e.getMessage());
        ApiResponse<ErrorResponse> apiResponse = ApiResponse.<ErrorResponse>builder()
                .result(errorResponse)
                .resultCode(HttpStatus.NOT_FOUND.value())
                .resultMsg("요청한 데이터가 존재하지 않습니다.")
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }

    // 인증 실패(아이디 또는 비밀번호 잘못 입력)
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleAuthenticationException(
            AuthenticationException e) {
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.UNAUTHORIZED_ERROR,
                e.getMessage());
        ApiResponse<ErrorResponse> apiResponse = ApiResponse.<ErrorResponse>builder()
                .result(errorResponse)
                .resultCode(HttpStatus.UNAUTHORIZED.value())
                .resultMsg("인증 실패했습니다.")
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
    }

    // @RequestParam, @PathVariable 값이 유효하지 않은 경우
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleBindException(BindException e) {
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.NOT_VALID_ERROR,
                e.getBindingResult());
        ApiResponse<ErrorResponse> apiResponse = ApiResponse.<ErrorResponse>builder()
                .result(errorResponse)
                .resultCode(HttpStatus.BAD_REQUEST.value())
                .resultMsg("유효하지 않은 입력입니다.")
                .build();
        return ResponseEntity.badRequest().body(apiResponse);
    }

    // 접근 권한이 없는 경우
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleAccessDeniedException(
            AccessDeniedException e) {
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.FORBIDDEN_ERROR, e.getMessage());
        ApiResponse<ErrorResponse> apiResponse = ApiResponse.<ErrorResponse>builder()
                .result(errorResponse)
                .resultCode(HttpStatus.FORBIDDEN.value())
                .resultMsg("접근 권한이 없습니다.")
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse);
    }

    // 기타 예상치 못한 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleException(Exception e) {
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR,
                e.getMessage());
        ApiResponse<ErrorResponse> apiResponse = ApiResponse.<ErrorResponse>builder()
                .result(errorResponse)
                .resultCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .resultMsg("서버 내부 오류가 발생했습니다.")
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }

    // 잘못된 비밀번호
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleBadCredentialsException(
            BadCredentialsException e) {
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.UNAUTHORIZED_ERROR,
                e.getMessage());
        ApiResponse<ErrorResponse> apiResponse = ApiResponse.<ErrorResponse>builder()
                .result(errorResponse)
                .resultCode(HttpStatus.UNAUTHORIZED.value())
                .resultMsg("잘못된 사용자명 또는 비밀번호입니다.")
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
    }

    // 잘못된 아이디
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleUsernameNotFoundException(
            UsernameNotFoundException e) {
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.NOT_FOUND_ERROR, e.getMessage());
        ApiResponse<ErrorResponse> apiResponse = ApiResponse.<ErrorResponse>builder()
                .result(errorResponse)
                .resultCode(HttpStatus.UNAUTHORIZED.value())
                .resultMsg("잘못된 사용자명 또는 비밀번호입니다.")
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
    }
}
