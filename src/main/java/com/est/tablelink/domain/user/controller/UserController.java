package com.est.tablelink.domain.user.controller;

import com.est.tablelink.domain.user.domain.User;
import com.est.tablelink.domain.user.dto.request.SignInUserRequest;
import com.est.tablelink.domain.user.dto.request.SignUpUserRequest;
import com.est.tablelink.domain.user.dto.request.UpdateUserRequest;
import com.est.tablelink.domain.user.dto.response.UserResponse;
import com.est.tablelink.domain.user.service.UserService;
import com.est.tablelink.domain.user.util.Role;
import com.est.tablelink.global.common.ApiResponse;
import com.est.tablelink.global.security.handler.CustomLogoutHandler;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:8081")
@RequiredArgsConstructor
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final CustomLogoutHandler customLogoutHandler;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponse>> signupUser(
            @Valid @RequestBody SignUpUserRequest signUpUserRequest) {
        if (!userService.isUsernameDuplicate(signUpUserRequest.getUsername())) {
            ApiResponse<UserResponse> apiResponse = ApiResponse.<UserResponse>builder()
                    .result(null)
                    .resultCode(HttpStatus.CONFLICT.value())
                    .resultMsg("이미 사용중인 아이디 입니다")
                    .build();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(apiResponse);
        }
        if (!userService.isNicknameDuplicate(signUpUserRequest.getNickname())) {
            ApiResponse<UserResponse> apiResponse = ApiResponse.<UserResponse>builder()
                    .result(null)
                    .resultCode(HttpStatus.CONFLICT.value())
                    .resultMsg("이미 사용중인 닉네임 입니다")
                    .build();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(apiResponse);
        }

        signUpUserRequest.setRole(Role.USER);
        User createdUser = userService.createUser(signUpUserRequest);
        UserResponse userResponse = UserResponse.toDto(createdUser);
        ApiResponse<UserResponse> apiResponse = ApiResponse.<UserResponse>builder()
                .result(userResponse)
                .resultCode(HttpStatus.CREATED.value())
                .resultMsg("회원가입 성공")
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    // 로그인
    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<Map<String, String>>> signinUser(
            @Valid @RequestBody SignInUserRequest signInUserRequest) {
        Map<String, String> tokens = userService.signinUser(signInUserRequest);
        ApiResponse<Map<String, String>> apiResponse = ApiResponse.<Map<String, String>>builder()
                .result(tokens)
                .resultCode(HttpStatus.OK.value())
                .resultMsg("로그인 성공")
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    // 회원 정보 상세 조회
    @GetMapping("/details")
    public ResponseEntity<ApiResponse<UserResponse>> getUserDetails() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        UserResponse userResponse = userService.getUserDetails(username);

        ApiResponse<UserResponse> apiResponse = ApiResponse.<UserResponse>builder()
                .result(userResponse)
                .resultCode(HttpStatus.OK.value())
                .resultMsg("회원 정보 조회 성공")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    // 회원정보 수정 로직
    @PostMapping("/update")
    public ResponseEntity<ApiResponse<String>> updateUser(
            @RequestBody UpdateUserRequest updateUserRequest) {
        String newAccessToken = userService.updateUser(updateUserRequest);
        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .result(newAccessToken)
                .resultCode(HttpStatus.OK.value())
                .resultMsg("회원정보 수정 성공")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
