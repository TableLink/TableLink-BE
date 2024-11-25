package com.est.tablelink.domain.user.service;

import com.est.tablelink.domain.token.domain.RefreshToken;
import com.est.tablelink.domain.token.repository.RefreshTokenRepository;
import com.est.tablelink.domain.user.domain.User;
import com.est.tablelink.domain.user.dto.request.admin.SignUpAdminRequest;
import com.est.tablelink.domain.user.dto.request.user.SignInUserRequest;
import com.est.tablelink.domain.user.dto.request.user.SignUpUserRequest;
import com.est.tablelink.domain.user.dto.request.user.UpdateUserRequest;
import com.est.tablelink.domain.user.dto.response.UserResponse;
import com.est.tablelink.domain.user.repository.UserRepository;
import com.est.tablelink.domain.user.util.Role;
import com.est.tablelink.global.security.provider.JwtTokenProvider;
import com.est.tablelink.global.security.service.CustomUserDetails;
import com.est.tablelink.global.security.service.CustomUserDetailsService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * ==========================<br>
     * User Registration Methods<br>
     * ==========================
     * */
    // 회원가입 메서드
    @Transactional
    public User createUser(SignUpUserRequest signUpUserRequest) {
        User user = signUpUserRequest.toEntity();
        user.encodePassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // 관리자 회원가입 메서드
    public User createdAdmin(SignUpAdminRequest signUpAdminRequest) {
        User user = signUpAdminRequest.toEntity();
        user.encodePassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // 아이디 중복 검사
    @Transactional(readOnly = true)
    public Boolean isUsernameDuplicate(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.isPresent();
    }

    // 닉네임 중복 검사
    @Transactional(readOnly = true)
    public Boolean isNicknameDuplicate(String nickname) {
        Optional<User> user = userRepository.findByNickname(nickname);
        return user.isPresent();
    }

    /**
     * ==========================<br>
     * Sign-in Methods<br>
     * ==========================
     * */

    // 일반 사용자 로그인 메서드
    @Transactional
    public Map<String, String> signinUser(SignInUserRequest signInUserRequest) {
        User user = getUser(signInUserRequest.getUsername());
        validatePassword(signInUserRequest.getPassword(), user);
        validateRole(user, Role.ADMIN);

        return generateTokens(user);
    }

    // 관리자 로그인 메서드
    @Transactional
    public Map<String, String> signinAdmin(SignInUserRequest signInUserRequest) {
        User user = getUser(signInUserRequest.getUsername());
        validatePassword(signInUserRequest.getPassword(), user);
        validateRole(user, Role.USER);

        return generateTokens(user);
    }

    /**
     * ==========================<br>
     * User Update Methods<br>
     * ==========================
     * */

    // 회원정보 수정
    @Transactional
    public String updateUser(UpdateUserRequest updateUserRequest) {
        String username = ((CustomUserDetails) getAuthentication().getPrincipal()).getUserResponse()
                .getUsername();
        User user = getUser(username);

        if (updateUserRequest.getPassword() != null && !updateUserRequest.getPassword().isEmpty()) {
            String passwordEncode = passwordEncoder.encode(updateUserRequest.getPassword());
            user.encodePassword(passwordEncode);
        }

        user.updateUser(user.getPassword(), updateUserRequest.getPhoneNumber(),
                updateUserRequest.getAddress(), updateUserRequest.getNickname());

        UserResponse userResponse = UserResponse.toDto(user);
        CustomUserDetails updatedUserDetails = new CustomUserDetails(userResponse,
                getAuthorities(user.getRole()));
        userRepository.save(user);

        // 필요한 경우, 클라이언트에 새로운 토큰을 반환하거나 저장하는 로직 추가
        return jwtTokenProvider.generateAccessToken(updatedUserDetails); // 새로 생성된 JWT 토큰 반환
    }

    /**
     * ==========================<br>
     * User Deletion Methods<br>
     * ==========================
     * */
    // 회원 탈퇴 메서드
    @Transactional
    public void deleteUser() {

        String username = ((CustomUserDetails) getAuthentication().getPrincipal()).getUserResponse()
                .getUsername();

        User user = getUser(username);

        // refreshToken 삭제
        refreshTokenRepository.deleteByUserUsername(username);
        // 유저 삭제
        userRepository.delete(user);
    }


    /**
     * ==========================<br>
     * User Helper Methods<br>
     * ==========================
     * */
    // 인증 정보 확인
    @Transactional(readOnly = true)
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    // 유저 조회
    private User getUser(String username) {

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }

    // db에 토큰 저장
    private void saveRefreshToken(User user, String refreshToken) {
        // Refresh Token 저장 로직 (예: Repository를 사용하여 DB에 저장)
        RefreshToken token = RefreshToken.builder()
                .refreshTokenValue(refreshToken)
                .user(user)
                .build();
        refreshTokenRepository.save(token);
    }

    // 권한 리스트를 생성하는 메서드
    private List<GrantedAuthority> getAuthorities(Role role) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role.getValue()));
        return authorities;
    }

    // 권한 확인
    private void validateRole(User user, Role role) {
        if (user.getRole().equals(role)) {
            throw new AccessDeniedException("해당 페이지에 로그인 할 수 없습니다.");
        }
    }

    // 비밀번호 확인
    private void validatePassword(String signInUserRequest, User user) {
        if (!passwordEncoder.matches(signInUserRequest, user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }
    }

    // 토큰 생성
    private Map<String, String> generateTokens(User user) {
        CustomUserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getUsername());

        // Access Token과 Refresh Token 생성
        String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        // Refresh Token을 DB에 저장하는 로직을 추가할 수 있습니다.
        saveRefreshToken(user, refreshToken);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        tokens.put("role", String.valueOf(userDetails.getUserResponse().getRole()));
        tokens.put("nickname", userDetails.getUserResponse().getNickname());

        return tokens;
    }

    // 사용자 상세 정보 불러오기
    @Transactional
    public UserResponse getUserDetails(String username) {
        User user = getUser(username);
        return UserResponse.toDto(user);
    }

}