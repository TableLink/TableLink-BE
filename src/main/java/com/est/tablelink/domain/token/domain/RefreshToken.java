package com.est.tablelink.domain.token.domain;

import com.est.tablelink.domain.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "token")
public class RefreshToken {

    @Id
    @Column(name = "refresh_token_key")
    private String refreshTokenKey;

    @Column(name = "refresh_token_value")
    private String refreshTokenValue;

    @Builder
    public RefreshToken(String refreshTokenKey, String refreshTokenValue){
        this.refreshTokenKey = refreshTokenKey;
        this.refreshTokenValue = refreshTokenValue;
    }

    @ManyToOne
    private User user;
}