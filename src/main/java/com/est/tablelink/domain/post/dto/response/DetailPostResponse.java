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

    private Long postId; // 게시글 id
    private String title; // 게시글 제목
    private String author; // 게시글 작성자 닉네임
    private String boardName; // 게시판 이름
    private String contentData; // 게시글 내용
    private String contentImage; // 이미지 데이터
    private LocalDateTime createdAt; // 게시글 생성 일시

    public static DetailPostResponse toDto(Post post, Content content) {
        return DetailPostResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .author(post.getAuthor().getNickname())
                .boardName(post.getBoard().getBoardName())
                .contentData(content.getData())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
