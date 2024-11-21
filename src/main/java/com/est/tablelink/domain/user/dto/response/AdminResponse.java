package com.est.tablelink.domain.user.dto.response;

import com.est.tablelink.domain.user.domain.User;
import com.est.tablelink.domain.user.util.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class AdminResponse {

    private Long id;
    private String username;
    private String password;
    private String phoneNumber;
    private String address;
    private String nickname;
    private Role role;

    public static AdminResponse toDto(User admin) {
        return AdminResponse.builder()
                .id(admin.getId())
                .username(admin.getUsername())
                .password(admin.getPassword())
                .phoneNumber(admin.getPhoneNumber())
                .address(admin.getAddress())
                .nickname(admin.getNickname())
                .role(admin.getRole())
                .build();
    }
}
