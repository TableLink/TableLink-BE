package com.est.tablelink.domain.user.controller;

import com.est.tablelink.domain.user.domain.User;
import com.est.tablelink.domain.user.dto.request.admin.SignUpAdminRequest;
import com.est.tablelink.domain.user.dto.request.user.SignInUserRequest;
import com.est.tablelink.domain.user.dto.response.AdminResponse;
import com.est.tablelink.domain.user.service.AdminService;
import com.est.tablelink.domain.user.service.UserService;
import com.est.tablelink.domain.user.util.Role;
import com.est.tablelink.global.common.ApiResponse;
import com.est.tablelink.global.common.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:8081")
@RequiredArgsConstructor
@Tag(name = "Admin Controller", description = "어드민 관련 API")
public class AdminController {

    private final UserService userService;
    private final AdminService adminService;

    // 회원가입
    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "관리자가 회원가입 할 때 사용하는 API")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Admin created successfully",
                    content = @Content(schema = @Schema(implementation = AdminResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Admin already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ApiResponse<AdminResponse>> createAdmin(
            @Valid @RequestBody SignUpAdminRequest signUpAdminRequest) {
        ResponseEntity<ApiResponse<AdminResponse>> result;
        if (userService.isUsernameDuplicate(signUpAdminRequest.getUsername())) {
            ApiResponse<AdminResponse> errorApi = ApiResponse.<AdminResponse>builder()
                    .result(null)
                    .resultCode(HttpStatus.CONFLICT.value())
                    .resultMsg("사용중인 아이디 입니다.")
                    .build();
            result = ResponseEntity.status(HttpStatus.CONFLICT).body(errorApi);
        } else if (userService.isNicknameDuplicate(signUpAdminRequest.getNickname())) {
            ApiResponse<AdminResponse> errorResponse = ApiResponse.<AdminResponse>builder()
                    .result(null)
                    .resultCode(HttpStatus.CONFLICT.value())
                    .resultMsg("이미 사용중인 닉네임 입니다")
                    .build();
            result = ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }else {
            signUpAdminRequest.setRole(Role.ADMIN);
            User createdAdmin = userService.createdAdmin(signUpAdminRequest);
            AdminResponse adminResponse = AdminResponse.toDto(createdAdmin);
            ApiResponse<AdminResponse> errorResponse = ApiResponse.<AdminResponse>builder()
                    .result(adminResponse)
                    .resultCode(HttpStatus.OK.value())
                    .resultMsg("회원가입 성공")
                    .build();
            result = ResponseEntity.status(HttpStatus.OK).body(errorResponse);
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
            @Valid @RequestBody SignInUserRequest signInUserRequest) {
        Map<String, String> tokens = userService.signinAdmin(signInUserRequest);
        ApiResponse<Map<String, String>> apiResponse = ApiResponse.<Map<String, String>>builder()
                .result(tokens)
                .resultCode(HttpStatus.OK.value())
                .resultMsg("로그인 성공")
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
