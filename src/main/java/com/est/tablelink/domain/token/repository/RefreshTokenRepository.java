package com.est.tablelink.domain.token.repository;


import com.est.tablelink.domain.token.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String>{

    void deleteByUserUsername(String username);
}
