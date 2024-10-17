package com.est.tablelink.domain.token.repository;


import com.est.tablelink.domain.token.domain.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String>{
    Optional<RefreshToken> findByRefreshTokenKey(String refreshTokenKey);
}
