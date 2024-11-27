package com.est.tablelink.domain.token.repository;


import com.est.tablelink.domain.token.domain.RefreshToken;
import com.est.tablelink.domain.user.domain.User;
import java.sql.Ref;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String>{

    void deleteByUserUsername(String username);

    // 만료된 리프레시 토큰을 삭제하는 쿼리
    @Query("DELETE FROM RefreshToken t WHERE t.expirationTime < CURRENT_TIMESTAMP")
    void deleteExpiredTokens();

    RefreshToken findByUserUsername(String username);
}
