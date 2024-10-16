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

    private String username;
    private String password;
    private String phoneNumber;
    private String address;
    private String nickname;

    public static UserResponse toDto(User user) {
        return UserResponse.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .nickname(user.getNickname())
                .build();
    }
}
