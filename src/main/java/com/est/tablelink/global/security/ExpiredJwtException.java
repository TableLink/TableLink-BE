package com.est.tablelink.global.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;

@Getter
public class ExpiredJwtException extends AuthenticationException {

    private final Header<?> header;
    private final Claims claims;

    public ExpiredJwtException(String message, Header<?> header, Claims claims) {
        super(message);
        this.header = header;
        this.claims = claims;
    }

}
