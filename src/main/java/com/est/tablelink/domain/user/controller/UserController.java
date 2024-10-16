package com.est.tablelink.domain.user.controller;

import com.est.tablelink.domain.user.domain.User;
import com.est.tablelink.domain.user.dto.request.SignUpUserRequest;
import com.est.tablelink.domain.user.dto.response.UserResponse;
import com.est.tablelink.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/api/user/signup")
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
}
