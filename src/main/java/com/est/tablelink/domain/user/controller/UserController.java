package com.est.tablelink.domain.user.controller;

import com.est.tablelink.domain.user.domain.User;
import com.est.tablelink.domain.user.dto.request.user.SignInUserRequest;
import com.est.tablelink.domain.user.dto.request.user.SignUpUserRequest;
import com.est.tablelink.domain.user.dto.request.user.UpdateUserRequest;
import com.est.tablelink.domain.user.dto.response.UserResponse;
import com.est.tablelink.domain.user.service.UserService;
import com.est.tablelink.domain.user.util.Role;
import com.est.tablelink.global.common.ApiResponse;
import com.est.tablelink.global.common.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:8081")
@RequiredArgsConstructor
@Tag(name = "User Controller", description = "사용자 관련 API")
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "일반 사용자가 회원가입 할 때 사용하는 API")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "User already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ApiResponse<UserResponse>> signupUser(
            @Valid @RequestBody SignUpUserRequest signUpUserRequest) {
        ResponseEntity<ApiResponse<UserResponse>> result;
        if (userService.isUsernameDuplicate(signUpUserRequest.getUsername())) {
            ApiResponse<UserResponse> errorResponse = ApiResponse.<UserResponse>builder()
                    .result(null)
                    .resultCode(HttpStatus.CONFLICT.value())
                    .resultMsg("이미 사용중인 아이디 입니다")
                    .build();
            result = ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        } else if (userService.isNicknameDuplicate(signUpUserRequest.getNickname())) {
            ApiResponse<UserResponse> errorResponse = ApiResponse.<UserResponse>builder()
                    .result(null)
                    .resultCode(HttpStatus.CONFLICT.value())
                    .resultMsg("이미 사용중인 닉네임 입니다")
                    .build();
            result = ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        } else {
            signUpUserRequest.setRole(Role.USER);
            User createdUser = userService.createUser(signUpUserRequest);
            UserResponse userResponse = UserResponse.toDto(createdUser);
            ApiResponse<UserResponse> successResponse = ApiResponse.<UserResponse>builder()
                    .result(userResponse)
                    .resultCode(HttpStatus.CREATED.value())
                    .resultMsg("회원가입 성공")
                    .build();
            result = ResponseEntity.status(HttpStatus.CREATED).body(successResponse);
        }

        return result;
    }

    // 로그인
    @PostMapping("/signin")
    @Operation(summary = "로그인", description = "일반 사용자가 로그인 할 때 사용하는 API")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 입력 데이터",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패: 잘못된 사용자명 또는 비밀번호",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "관리자가 로그인 하려할때",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ApiResponse<Map<String, String>>> signinUser(
            @Valid @RequestBody SignInUserRequest signInUserRequest, HttpServletResponse response) {
        Map<String, String> tokens = userService.signinUser(signInUserRequest, response);
        ApiResponse<Map<String, String>> successResponse = ApiResponse.<Map<String, String>>builder()
                .result(tokens)
                .resultCode(HttpStatus.OK.value())
                .resultMsg("로그인 성공")
                .build();
        return ResponseEntity.ok(successResponse);
    }

    // 회원 정보 상세 조회
    @GetMapping
    @Operation(summary = "회원 정보 상세 조회", description = "일반 사용자가 본인의 상세 정보를 조회 할 때 사용하는 API")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원 정보 조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "회원 정보를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ApiResponse<UserResponse>> getUserDetails() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        UserResponse userResponse = userService.getUserDetails(username);

        ApiResponse<UserResponse> successResponse = ApiResponse.<UserResponse>builder()
                .result(userResponse)
                .resultCode(HttpStatus.OK.value())
                .resultMsg("회원 정보 조회 성공")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(successResponse);
    }

    // 회원정보 수정
    @PatchMapping
    @Operation(summary = "회원 정보 수정", description = "일반 사용자가 본인의 비밀번호, 전화번호, 주소, 닉네임 등을 수정 할 때 사용하는 API")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원정보 수정 성공",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 입력 데이터",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "회원 정보를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ApiResponse<String>> updateUser(
            @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        String newAccessToken = userService.updateUser(updateUserRequest);
        ApiResponse<String> successResponse = ApiResponse.<String>builder()
                .result(newAccessToken)
                .resultCode(HttpStatus.OK.value())
                .resultMsg("회원정보 수정 성공")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(successResponse);
    }

    // 회원 탈퇴
    @DeleteMapping
    @Operation(summary = "회원 탈퇴", description = "일반 사용자가 회원 탈퇴 할 때 사용하는 API")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원 탈퇴 성공",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "회원 정보를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ApiResponse<String>> deleteUser() {
        userService.deleteUser();
        ApiResponse<String> successResponse = ApiResponse.<String>builder()
                .result("회원 탈퇴 성공")
                .resultCode(HttpStatus.OK.value())
                .resultMsg("회원 탈퇴 성공")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(successResponse);
    }
}
