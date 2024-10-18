package com.est.tablelink.global.security.handler;

import com.est.tablelink.domain.token.repository.RefreshTokenRepository;
import com.est.tablelink.global.security.provider.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Component
@Slf4j
public class CustomLogoutHandler implements LogoutHandler {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) {
        String token = resolveToken(request.getHeader("Authorization"));
        if (token != null) {
            String username = jwtTokenProvider.getUsernameFromToken(token);
            if (username != null) {
                try {
                    refreshTokenRepository.deleteByUserUsername(username);
                    log.info("Successfully logged out and deleted refresh token for user: {}", username);
                } catch (Exception e) {
                    log.error("Failed to delete refresh token for user: {}", username, e);
                }
            } else {
                log.warn("Username could not be extracted from token");
            }
        } else {
            log.warn("No token found in Authorization header");
        }
    }

    private String resolveToken(String authorization) {
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return null;
    }
}
