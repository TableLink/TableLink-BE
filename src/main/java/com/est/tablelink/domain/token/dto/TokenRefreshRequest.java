package com.est.tablelink.domain.token.dto;

import com.est.tablelink.domain.token.domain.RefreshToken;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class TokenRefreshRequest {

    private String refreshToken;

    public RefreshToken toEntity() {
        return RefreshToken.builder()
                .refreshTokenValue(refreshToken)
                .build();
    }
}
