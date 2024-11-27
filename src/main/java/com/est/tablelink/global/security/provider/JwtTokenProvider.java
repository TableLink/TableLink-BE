package com.est.tablelink.global.security.provider;

import com.est.tablelink.domain.token.service.InvalidTokenException;
import com.est.tablelink.global.security.service.CustomUserDetails;
import com.est.tablelink.global.security.service.CustomUserDetailsService;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * JwtTokenProvider는 JWT 토큰을 생성, 검증 및 관리하는 유틸리티 클래스입니다.
 * Spring Security의 UserDetails 서비스를 사용하여 사용자 인증 정보를 처리합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final CustomUserDetailsService customUserDetailsService;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration-time}")
    private Long expirationTime;

    @Getter
    @Value("${jwt.refresh-expiration-time}")
    private Long refreshExpirationTime;

    /**
     * 지정된 사용자에 대해 refresh 토큰을 생성합니다.
     *
     * @param customUserDetails refresh 토큰을 생성할 사용자 정보
     * @return 생성된 refresh 토큰
     */
    public String generateRefreshToken(CustomUserDetails customUserDetails) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(customUserDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpirationTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    /**
     * 지정된 사용자에 대해 access 토큰을 생성합니다.
     * 사용자 정보가 CustomUserDetails 인스턴스일 경우, 사용자 권한도 토큰에 포함됩니다.
     *
     * @param userDetails access 토큰을 생성할 사용자 정보
     * @return 생성된 access 토큰
     */
    public String generateAccessToken(CustomUserDetails customUserDetails) {
        Map<String, Object> claims = new HashMap<>();

        if (customUserDetails != null) {
            claims.put("role", customUserDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));
        }

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(customUserDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    /**
     * 사용자에 대해 access 토큰과 refresh 토큰을 동시에 생성하여 반환합니다.
     *
     * @param customUserDetails 토큰을 생성할 사용자 정보
     * @return access 토큰과 refresh 토큰을 포함한 맵
     */
    public Map<String, String> generateTokens(CustomUserDetails customUserDetails) {
        String accessToken = generateAccessToken(customUserDetails);
        String refreshToken = generateRefreshToken(customUserDetails);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        return tokens;
    }

    /**
     * JWT 토큰에서 사용자 이름을 추출합니다.
     *
     * @param token 사용자 이름을 추출할 JWT 토큰
     * @return 추출된 사용자 이름
     */
    public String getUsernameFromToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Error message: {}", e.getMessage());
            return null;
        }
    }

    /**
     * JWT 토큰이 유효한지 검증합니다.
     * 사용자 이름을 비교하고, 토큰이 만료되지 않았는지 확인합니다.
     *
     * @param token 검증할 JWT 토큰
     * @param customUserDetails 비교할 사용자 정보
     * @return 토큰이 유효하면 true, 그렇지 않으면 false
     */
    public boolean validateToken(String token, CustomUserDetails customUserDetails) {
        String username = getUsernameFromToken(token);
        return username.equals(customUserDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * JWT 토큰이 만료되었는지 확인합니다.
     *
     * @param token 확인할 JWT 토큰
     * @return 토큰이 만료되었으면 true, 그렇지 않으면 false
     */
    public boolean isTokenExpired(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }

    /**
     * 유효한 refresh 토큰을 사용하여 새로운 access 토큰을 생성합니다.
     *
     * @param refreshToken 새로 생성할 access 토큰을 위한 refresh 토큰
     * @return 생성된 access 토큰
     * @throws InvalidTokenException refresh 토큰이 만료되었거나 유효하지 않은 경우 예외를 발생시킴
     */
    public String refreshAccessToken(String refreshToken) throws InvalidTokenException{
        String username = getUsernameFromToken(refreshToken);
        if (username != null || isTokenExpired(refreshToken)) {
            throw new InvalidTokenException("유효하지 않거나 만료된 refresh 토큰입니다.");
        }
        CustomUserDetails customUserDetails = customUserDetailsService.loadUserByUsername(username);
        return generateTokens(customUserDetails).toString();
    }
}