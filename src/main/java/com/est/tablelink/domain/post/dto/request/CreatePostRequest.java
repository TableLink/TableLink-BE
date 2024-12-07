package com.est.tablelink.domain.post.dto.request;

import com.est.tablelink.domain.board.domain.Board;
import com.est.tablelink.domain.post.domain.Content;
import com.est.tablelink.domain.post.domain.Post;
import com.est.tablelink.domain.user.domain.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for {@link com.est.tablelink.domain.post.domain.Post}
 */

@Getter
@Setter
@Builder
public class CreatePostRequest {

    @NotBlank(message = "제목을 작성해 주세요.")
    @Size(max = 50, message = "제목은 50자 내로 작성해 주세요.")
    private String title;

    @NotBlank(message = "내용을 작성해 주세요.")
    private String contentText;

//    @NotBlank(message = "내용을 작성해 주세요.")
//    private String contentImage;

    public Post toEntity() {
        return Post.builder()
                .title(this.title) // 예시로 제목을 설정
                .build();
    }
}