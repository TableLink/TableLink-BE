package com.est.tablelink.domain.user.controller;

import com.est.tablelink.domain.user.domain.User;
import com.est.tablelink.domain.user.dto.request.CreateUserRequest;
import com.est.tablelink.domain.user.dto.request.SignInRequest;
import com.est.tablelink.domain.user.dto.response.UserResponse;
import com.est.tablelink.domain.user.repository.UserRepository;
import com.est.tablelink.domain.user.service.UserService;
import com.est.tablelink.global.security.CustomUserDetails;
import com.est.tablelink.global.security.CustomUserDetailsService;
import com.est.tablelink.global.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;
    private final UserRepository userRepository;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<UserResponse> createUser(
            @RequestBody @Valid CreateUserRequest createUserRequest) {

        if (!userService.isUsernameDuplicate(createUserRequest.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .build();
        } else if (!userService.isNicknameDuplicate(createUserRequest.getNickname())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .build();
        } else {
            createUserRequest.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
            User user = userService.createUser(createUserRequest);
            return ResponseEntity.status(HttpStatus.OK).body(UserResponse.toDto(user));
        }
    }

    // 로그인
    @PostMapping("/signin")
    public ResponseEntity<UserResponse> SignInUser(
            @RequestBody @Valid SignInRequest signInRequestDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(signInRequestDto.getUsername(),
                            signInRequestDto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // JWT 토큰을 생성합니다.
            CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(
                    signInRequestDto.getUsername());
            String token = jwtUtil.generateToken(userDetails.getUsername());

            // JWT 토큰을 반환합니다.
            UserResponse userResponseDto = userService.getUserResponseDtoByUserName(userDetails.getUsername());

            userResponseDto.setToken(token);
            return ResponseEntity.ok(userResponseDto);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

//    // 회원정보 수정
//    @PatchMapping("/edit/{username}/update")
//    public ResponseEntity<UserResponseDto> updateUser(@PathVariable("username") String username,
//            @Valid
//            UpdateUserRequest updateUserRequest, BindingResult bindingResult) {
//
//        if (bindingResult.hasErrors()) {
//            return ResponseEntity.badRequest().build();
//        }
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() == null) {
//            log.warn("Authentication object is null or not authenticated");
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//
//        CustomUserDetails principal = (CustomUserDetails) SecurityContextHolder.getContext()
//                .getAuthentication().getPrincipal();
//        User user = userRepository.findByUsername(principal.getUsername())
//                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
//        user.updateUser(updateUserRequest.getPassword(), updateUserRequest.getPhoneNumber(),
//                updateUserRequest.getAddress(), updateUserRequest.getNickname());
//        User updatedUser = userService.updateUser(username, user);
//        return ResponseEntity.status(HttpStatus.OK).body(UserResponseDto.toDto(updatedUser));
//    }
}
