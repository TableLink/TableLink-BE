package com.est.tablelink.global.security.filter;

import com.est.tablelink.global.security.provider.JwtTokenProvider;
import com.est.tablelink.global.security.service.CustomUserDetails;
import com.est.tablelink.global.security.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // Authorization 헤더에서 JWT 토큰 추출
        String authorizationHeader = request.getHeader("Authorization");
        String refreshTokenHeader = request.getHeader("Refresh-Token");
        String token = null;
        String username = null;

        String requestURI = request.getRequestURI();
        if (requestURI.equals("/api/user/signup")){
            filterChain.doFilter(request, response);
            return;
        }

        log.info("Authorization Header: {}", authorizationHeader); // 헤더 값 확인

        // Bearer 토큰 형식이 맞는지 확인
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
            try {
                username = jwtTokenProvider.getUsernameFromToken(token);
                if (username == null) {
                    log.error("Username is null after extracting from token");
                }
            } catch (Exception e) {
                log.error("JWT 토큰 검증 오류: {}", e.getMessage());
            }
        }

        // 토큰이 존재하고, 시큐리티 컨텍스트에 인증 정보가 없는 경우 처리
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            CustomUserDetails customUserDetails = customUserDetailsService.loadUserByUsername(username);

            // 토큰이 유효한 경우, 시큐리티 컨텍스트에 인증 정보 설정
            if (jwtTokenProvider.validateToken(token, customUserDetails)) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(customUserDetails, null,
                                customUserDetails.getAuthorities());
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else if (refreshTokenHeader != null) {
                if (jwtTokenProvider.isTokenExpired(refreshTokenHeader)){
                    log.error("Refresh token is expired");
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Refresh token is expired");
                    return;
                }
            }
        }

        // 다음 필터 체인으로 요청 전달
        filterChain.doFilter(request, response);
    }
}
