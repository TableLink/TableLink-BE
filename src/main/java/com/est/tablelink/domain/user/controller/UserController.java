package com.est.tablelink.domain.user.controller;

import com.est.tablelink.domain.user.domain.User;
import com.est.tablelink.domain.user.dto.request.SignInUserRequest;
import com.est.tablelink.domain.user.dto.request.SignUpUserRequest;
import com.est.tablelink.domain.user.dto.response.UserResponse;
import com.est.tablelink.domain.user.service.UserService;
import com.est.tablelink.global.security.handler.CustomLogoutHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CustomLogoutHandler customLogoutHandler;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signupUser(
            @Valid @RequestBody SignUpUserRequest signUpUserRequest) {
        if (!userService.isUsernameDuplicate(signUpUserRequest.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
        if (!userService.isNicknameDuplicate(signUpUserRequest.getNickname())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
        User createdUser = userService.createUser(signUpUserRequest);
        UserResponse userResponse = UserResponse.toDto(createdUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    // 로그인
    @PostMapping("/signin")
    public ResponseEntity<Map<String, String>> signinUser(
            @Valid @RequestBody SignInUserRequest signInUserRequest) {
        Map<String, String> tokens = userService.signinUser(signInUserRequest);
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {

        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        // 로그아웃 처리
        logoutHandler.logout(request, response,
                SecurityContextHolder.getContext().getAuthentication());

        customLogoutHandler.logout(request, response, SecurityContextHolder.getContext().getAuthentication());

        return ResponseEntity.ok("Logout successful");
    }

    /*
    // 로그인
    @PostMapping("/signin")
    public ResponseEntity<String> signinUser(
            @Valid @RequestBody SignInUserRequest signInUserRequest) {
        String token = userService.signinUser(signInUserRequest);

        return ResponseEntity.ok(token);
    }*/

    /*// 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(HttpServletRequest request) {
        String refreshToken = userService.resolveToken(request.getHeader("authorizationHeader"));
        try {
            userService.logoutUser(refreshToken);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("RefreshToken not found");
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.OK).body("Successfully logged out");
        }

    }*/
}
