package com.est.tablelink.domain.user.service;

import com.est.tablelink.domain.post.repository.PostRepository;
import com.est.tablelink.domain.token.repository.RefreshTokenRepository;
import com.est.tablelink.domain.user.repository.UserRepository;
import com.est.tablelink.global.security.provider.JwtTokenProvider;
import com.est.tablelink.global.security.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;




}
