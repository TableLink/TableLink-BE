package com.est.tablelink.domain.user.dto.request.admin;

import com.est.tablelink.domain.user.domain.User;
import com.est.tablelink.domain.user.util.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for {@link User}
 */

@Builder
@Getter
@Setter
public class SignUpAdminRequest {

    // RFC 5322 이메일 정규식 형태로 작성
    @Pattern(regexp = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+\\.[a-zA-z]{2,}$", message = "아이디는 이메일 형태로 입력해주세요.")
    @NotBlank(message = "아이디는 빈칸을 입력할 수 없습니다.")
    private String username;

    // 비밀번호: 영문, 숫자, 특수문자를 포함하여 8~15자 작성
    @Size(min = 8, max = 16, message = "비밀번호는 8자 이상 16자 이하여야 합니다.")
    @Pattern(regexp = "^(?:(?=.*[a-zA-Z])(?=.*[0-9])|(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])|(?=.*[0-9])(?=.*[!@#$%^*+=-])).*$",
            message = "비밀번호는 영문, 숫자, 특수문자 중 2가지 종류를 포함해야 합니다.")
    @NotBlank(message = "비밀번호는 빈칸을 입력할 수 없습니다.")
    private String password;

    @Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$", message = "전화번호 양식에 맞게 작성해 주세요.")
    private String phoneNumber;

    @NotBlank(message = "빈칸이 들어갈 수 없습니다.")
    private String address;

    @NotBlank(message = "빈칸이 들어갈 수 없습니다.")
    private String nickname;

    private Role role;

    public User toEntity() {
        return User.builder()
                .username(username)
                .password(password)
                .phoneNumber(phoneNumber)
                .address(address)
                .nickname(nickname)
                .role(role)
                .build();
    }
}