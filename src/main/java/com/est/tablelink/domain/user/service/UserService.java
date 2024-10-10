package com.est.tablelink.domain.user.service;

import com.est.tablelink.domain.user.domain.User;
import com.est.tablelink.domain.user.dto.request.CreateUserRequest;
import com.est.tablelink.domain.user.dto.response.UserResponse;
import com.est.tablelink.domain.user.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 회원가입 메서드
    @Transactional
    public User createUser(CreateUserRequest createUserRequest) {
        return userRepository.save(createUserRequest.toEntity());
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
