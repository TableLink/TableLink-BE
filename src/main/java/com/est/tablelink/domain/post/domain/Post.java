package com.est.tablelink.domain.post.domain;

import com.est.tablelink.domain.board.domain.Board;
import com.est.tablelink.domain.user.domain.User;
import com.est.tablelink.domain.user.util.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "post")
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;  // 게시글 id

    @Column(name = "title")
    private String title; // 제목

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User author; // 작성자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board; // 게시판 id

    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Content content; // 게시글 내용

    // 작성자 검증 메서드 (삭제 권한 체크 등)
    public boolean canModifyOrDelete(User user) {
        return this.author.equals(user);
    }

    @Builder
    public Post(Long id, String title, User author, Board board, Content content) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.board = board;
        this.content = content;
    }

    // 게시글 업데이트
    public void updatePost(String title, Content content) {
        this.title = title;
        this.content = content;
    }

    public void setContent(Content content) {
        this.content = content;
    }
}
