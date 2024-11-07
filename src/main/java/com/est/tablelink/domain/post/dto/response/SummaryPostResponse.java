package com.est.tablelink.domain.post.dto.response;

import com.est.tablelink.domain.post.domain.Post;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SummaryPostResponse {

    private Long id;
    private String title;
    private String author;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static SummaryPostResponse toDto(Post post) {
        return SummaryPostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .author(post.getAuthor().getNickname())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
