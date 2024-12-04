package com.est.tablelink.domain.token.controller;

import com.est.tablelink.domain.token.dto.TokenRefreshRequest;
import com.est.tablelink.domain.token.service.TokenService;
import com.est.tablelink.global.common.ApiResponse;
import com.est.tablelink.global.security.provider.JwtTokenProvider;
import com.est.tablelink.global.security.service.CustomUserDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/token")
@CrossOrigin(origins = "https://localhost:8081")
@Tag(name = "Token Controller", description = "토큰 관련 API")
public class TokenController {

    private final TokenService tokenService;
    private final JwtTokenProvider jwtTokenProvider;

    // 리프레시 토큰으로 새로운 액세스 토큰을 생성하는 엔드포인트
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Map<String, String>>> refreshAccessToken(
            @CookieValue("refreshToken") TokenRefreshRequest tokenRefreshRequest,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        String refreshToken = tokenRefreshRequest.getRefreshToken();

        // 리프레시 토큰 유효성 검사
        if (!jwtTokenProvider.validateToken(refreshToken, customUserDetails)) {
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid or expired refresh token");
        }

        // 리프레시 토큰에서 사용자 정보 추출
        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);

        // refreshToken 만료여부 확인
        if (tokenService.isRefreshTokenExpired(username)) {
            return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Expired refresh token");
        }

        // 새로운 액세스 토큰 생성
        String newAccessToken = jwtTokenProvider.generateAccessToken(customUserDetails);

        // 새로운 액세스 토큰 반환
        Map<String, String> response = new HashMap<>();
        response.put("accessToken", newAccessToken);

        return buildSuccessResponse(HttpStatus.OK, "Access token refreshed successfully", response);
    }


    private ResponseEntity<ApiResponse<Map<String, String>>> buildErrorResponse(
            HttpStatus status, String message) {
        ApiResponse<Map<String, String>> errorResponse = ApiResponse.<Map<String, String>>builder()
                .result(null)
                .resultCode(status.value())
                .resultMsg(message)
                .build();
        return ResponseEntity.status(status).body(errorResponse);
    }

    // 성공 응답 생성 메서드
    private ResponseEntity<ApiResponse<Map<String, String>>> buildSuccessResponse(HttpStatus status,
            String message, Map<String, String> result) {
        ApiResponse<Map<String, String>> successResponse = ApiResponse.<Map<String, String>>builder()
                .resultCode(status.value())
                .resultMsg(message)
                .result(result)
                .build();
        return ResponseEntity.status(status).body(successResponse);
    }

}
