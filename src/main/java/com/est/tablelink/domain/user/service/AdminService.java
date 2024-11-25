package com.est.tablelink.domain.user.service;

import com.est.tablelink.domain.post.repository.PostRepository;
import com.est.tablelink.domain.token.repository.RefreshTokenRepository;
import com.est.tablelink.domain.user.domain.User;
import com.est.tablelink.domain.user.dto.response.UserResponse;
import com.est.tablelink.domain.user.repository.UserRepository;
import com.est.tablelink.domain.user.util.Role;
import com.est.tablelink.global.security.provider.JwtTokenProvider;
import com.est.tablelink.global.security.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Transactional
    public Page<UserResponse> getUserPage(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<User> users;

        if (keyword != null && !keyword.isEmpty()) {
            users = userRepository.findByRoleAndKeyword(Role.USER, keyword, pageable);
        } else {
            users = userRepository.findByRole(Role.USER, pageable);
        }
        return users.map(UserResponse::toDto);
    }
}
