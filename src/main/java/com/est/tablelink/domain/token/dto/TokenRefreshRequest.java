package com.est.tablelink.domain.token.dto;

import com.est.tablelink.domain.token.domain.RefreshToken;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class TokenRefreshRequest {

    private String refreshToken;

    @JsonCreator
    public TokenRefreshRequest(@JsonProperty("refreshToken") String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public RefreshToken toEntity() {
        return RefreshToken.builder()
                .refreshTokenValue(refreshToken)
                .build();
    }
}
