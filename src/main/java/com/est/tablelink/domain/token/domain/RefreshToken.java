package com.est.tablelink.domain.token.domain;

import com.est.tablelink.domain.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "refresh_token")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id")
    private Long id;

    @Column(name = "refresh_token_value")
    private String refreshTokenValue;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", nullable = false, referencedColumnName = "username")
    private User user;

    @Column(name = "expirationTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expirationTime;

    @Builder
    public RefreshToken(String refreshTokenValue, User user, Date expirationTime){
        this.refreshTokenValue = refreshTokenValue;
        this.user = user;
        this.expirationTime = expirationTime;
    }
}