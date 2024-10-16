package com.est.tablelink.domain.user.service;

import com.est.tablelink.domain.user.domain.User;
import com.est.tablelink.domain.user.dto.request.SignInUserRequest;
import com.est.tablelink.domain.user.dto.request.SignUpUserRequest;
import com.est.tablelink.domain.user.dto.response.UserResponse;
import com.est.tablelink.domain.user.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입 메서드
    @Transactional
    public User createUser(SignUpUserRequest signUpUserRequest) {
        User user = signUpUserRequest.toEntity();
        user.encodePassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // 아이디 중복 검사
    @Transactional(readOnly = true)
    public Boolean isUsernameDuplicate(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.isEmpty();
    }

    // 닉네임 중복 검사
    @Transactional(readOnly = true)
    public Boolean isNicknameDuplicate(String nickname) {
        Optional<User> user = userRepository.findByNickname(nickname);
        return user.isEmpty();
    }

    // 로그인 메서드
    @Transactional(readOnly = true)
    public User signinUser(SignInUserRequest signInUserRequest) {
        User user = userRepository.findByUsername(signInUserRequest.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(signInUserRequest.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        return user; // JWT 토큰 생성 로직으로 넘어가기 전에 사용자 정보를 반환
    }

//    // 회원정보 수정
//    @Transactional
//    public UserResponseDto updateUser(String username, UpdateUserRequest updateUserRequest) {
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
//
//        // 사용자 정보 업데이트
//        user.updateUser(
//                updateUserRequest.getPassword(),
//                updateUserRequest.getPhoneNumber(),
//                updateUserRequest.getAddress(),
//                updateUserRequest.getNickname()
//        );
//
//        // 변경된 사용자 정보를 저장하고 DTO로 변환하여 반환
//        User updatedUser = userRepository.save(user);
//        return UserResponseDto.toDto(updatedUser);
//    }

    public UserResponse getUserResponseDtoByUserName(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        return UserResponse.toDto(user);
    }
}
