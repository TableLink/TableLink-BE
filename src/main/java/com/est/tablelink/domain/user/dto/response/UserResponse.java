package com.est.tablelink.domain.user.dto.response;

import com.est.tablelink.domain.user.domain.User;
import com.est.tablelink.domain.user.util.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UserResponse {

    private Long id;
    private String username;
    private String password;
    private String phoneNumber;
    private String address;
    private String nickname;
    private Role role;

    public static UserResponse toDto(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .nickname(user.getNickname())
                .role(user.getRole())
                .build();
    }
}
