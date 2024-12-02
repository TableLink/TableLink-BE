package com.est.tablelink.domain.token.service;

import com.est.tablelink.domain.token.domain.RefreshToken;
import com.est.tablelink.domain.token.repository.RefreshTokenRepository;
import com.est.tablelink.domain.user.service.UserService;
import com.est.tablelink.global.security.provider.JwtTokenProvider;
import com.est.tablelink.global.security.service.CustomUserDetails;
import com.est.tablelink.global.security.service.CustomUserDetailsService;
import java.util.Date;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final RefreshTokenRepository refreshTokenRepository;


    public boolean isRefreshTokenExpired(String username) {
        Optional<RefreshToken> refreshToken = Optional.ofNullable(refreshTokenRepository.findByUserUsername(username));

        return refreshToken
                .map(token -> token.getExpirationTime().before(new Date()))
                .orElse(true); // 리프레시 토큰이 없으면 만료된 것으로 처리
    }

    /**
     * 만료된 리프레시 토큰을 DB에서 삭제하는 스케줄링 메서드
     * 이 메서드는 매일 자정(00:00)에 실행됩니다.
     */
    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    public void deleteExpiredRefreshTokens() {
        // 만료된 리프레시 토큰을 DB에서 삭제하는 로직
        refreshTokenRepository.deleteExpiredTokens(); // 만료된 토큰을 삭제하는 메서드
    }

    public boolean isTokenValid(String refreshToken, String username) {
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByRefreshTokenValueAndUserUsername(refreshToken, username);

        return optionalRefreshToken
                .map(token -> token.getExpirationTime().after(new Date()))
                .orElse(false); // 만약 없으면 유효하지 않다고 처리
    }
}
