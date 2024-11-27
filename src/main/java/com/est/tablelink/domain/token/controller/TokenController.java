package com.est.tablelink.domain.token.controller;

import com.est.tablelink.domain.token.dto.TokenRefreshRequest;
import com.est.tablelink.domain.token.service.TokenService;
import com.est.tablelink.domain.user.domain.User;
import com.est.tablelink.domain.user.dto.response.UserResponse;
import com.est.tablelink.domain.user.service.UserService;
import com.est.tablelink.global.common.ApiResponse;
import com.est.tablelink.global.security.provider.JwtTokenProvider;
import com.est.tablelink.global.security.service.CustomUserDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.View;

@RestController
@AllArgsConstructor
@RequestMapping("/api/token")
@CrossOrigin(origins = "http://localhost:8081")
@Tag(name = "Token Controller", description = "토큰 관련 API")
public class TokenController {

    private final TokenService tokenService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final View error;

    // 리프레시 토큰으로 새로운 액세스 토큰을 생성하는 엔드포인트
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Map<String, String>>> refreshAccessToken(
            @RequestBody TokenRefreshRequest tokenRefreshRequest,
            CustomUserDetails customUserDetails) {
        String refreshToken = tokenRefreshRequest.getRefreshToken();

        // 리프레시 토큰 유효성 검사
        if (!jwtTokenProvider.validateToken(refreshToken, customUserDetails)) {
            ApiResponse<Map<String, String>> errorResponse = ApiResponse.<Map<String, String>>builder()
                    .result(null)
                    .resultCode(HttpStatus.UNAUTHORIZED.value())
                    .resultMsg("Invalid or expired refresh token")
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        // 리프레시 토큰이 만료되었는지 확인
        String username = customUserDetails.getUsername();
        if (tokenService.isRefreshTokenExpired(username)) {
            ApiResponse<Map<String, String>> errorResponse = ApiResponse.<Map<String, String>>builder()
                    .result(null)
                    .resultCode(HttpStatus.UNAUTHORIZED.value())
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        // 새로운 액세스 토큰 생성
        String newAccessToken = jwtTokenProvider.generateAccessToken(customUserDetails);

        // 새로운 액세스 토큰 반환
        Map<String, String> response = new HashMap<>();
        response.put("accessToken", newAccessToken);

        ApiResponse<Map<String, String>> successResponse = ApiResponse.<Map<String, String>>builder()
                .resultCode(HttpStatus.OK.value())
                .resultMsg("Access token refreshed successfully")
                .result(response)
                .build();

        return ResponseEntity.ok(successResponse);
    }

}
