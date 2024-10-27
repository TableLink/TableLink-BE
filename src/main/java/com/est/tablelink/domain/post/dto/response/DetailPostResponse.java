package com.est.tablelink.domain.post.dto.response;

import com.est.tablelink.domain.post.domain.Content;
import com.est.tablelink.domain.post.domain.Post;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DetailPostResponse {
    private Long id; // 게시글 id
    private String title; // 게시글 제목
    private String author; // 게시글 작성자 닉네임
    private Content content; // 게시글 내용
    private LocalDateTime createdAt; // 게시글 생성 일시

    public static DetailPostResponse toDto(Post post){
        return DetailPostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .author(post.getAuthor().getNickname())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
