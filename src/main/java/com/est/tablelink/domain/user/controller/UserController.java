package com.est.tablelink.domain.user.controller;

import com.est.tablelink.domain.user.dto.request.CreateUserRequest;
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
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> createUser(
            @RequestBody @Valid CreateUserRequest createUserRequest) {
//        try {
//            createUserRequest.setPassword();
//        }
        return ResponseEntity.status(HttpStatus.OK).body("회원가입 성공");
    }
}
