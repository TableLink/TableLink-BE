package com.est.tablelink.domain.user.domain;

import com.est.tablelink.domain.user.util.BaseEntity;
import com.est.tablelink.domain.user.util.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Entity
@NoArgsConstructor
@Getter
public class User extends BaseEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public User(String username, String password, String phoneNumber, String address,
            String nickname, Role role) {
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.nickname = nickname;
        this.role = role;
    }

    // 연관 관계 편의 메서드
    public void encodePassword(String password) {
        this.password = password;
    }

    public void updateUser(String password, String phoneNumber, String address, String nickname) {
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.nickname = nickname;
    }
}
