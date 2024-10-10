package com.est.tablelink.domain.user.dto.request;

import com.est.tablelink.domain.user.domain.User;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for {@link com.est.tablelink.domain.user.domain.User}
 */

@Builder
@Getter
@Setter
public class UpdateUserRequest implements Serializable {

    private String password;
    private String phoneNumber;
    private String address;
    private String nickname;
}