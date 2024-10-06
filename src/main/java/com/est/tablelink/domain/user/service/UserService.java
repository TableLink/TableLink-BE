package com.est.tablelink.domain.user.service;

import com.est.tablelink.domain.user.dto.request.CreateUserRequest;
import com.est.tablelink.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 회원가입 메서드
    @Transactional
    public void createUser (CreateUserRequest createUserRequest){
        userRepository.save(createUserRequest.toEntity());
    }
}
