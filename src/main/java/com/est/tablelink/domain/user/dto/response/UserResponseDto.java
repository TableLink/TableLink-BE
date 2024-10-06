package com.est.tablelink.domain.user.dto.response;

import com.est.tablelink.domain.user.domain.User;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserResponseDto {

    private String username;
    private String password;
    private String phoneNumber;
    private String address;
    private String nickname;

    public static UserResponseDto toDto(User user) {
        return UserResponseDto.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .nickname(user.getNickname())
                .build();
    }
}
