package com.est.tablelink.domain.post.service;

import com.est.tablelink.domain.board.domain.Board;
import com.est.tablelink.domain.board.repository.BoardRepository;
import com.est.tablelink.domain.post.domain.Content;
import com.est.tablelink.domain.post.domain.Post;
import com.est.tablelink.domain.post.dto.request.CreatePostRequest;
import com.est.tablelink.domain.post.dto.response.DetailPostResponse;
import com.est.tablelink.domain.post.dto.response.SummaryPostResponse;
import com.est.tablelink.domain.post.repository.ContentRepository;
import com.est.tablelink.domain.post.repository.PostRepository;
import com.est.tablelink.domain.user.domain.User;
import com.est.tablelink.domain.user.repository.UserRepository;
import com.est.tablelink.domain.user.service.UserService;
import com.est.tablelink.global.security.service.CustomUserDetails;
import java.util.NoSuchElementException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final ContentRepository contentRepository;
    private final UserService userService;

    /**
     * 게시글 생성 메서드
     */
    @Transactional
    public DetailPostResponse createPost(CreatePostRequest createPostRequest, Long boardId) {
        // 사용자 정보 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));

        // 게시판 확인
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 게시판입니다."));

        // Post 엔티티 생성
        Post post = Post.builder()
                .title(createPostRequest.getTitle())
                .author(user) // 작성자
                .board(board) // 게시판
                .build();

        postRepository.save(post);

        // 콘텐츠 저장
        Content content = Content.builder()
                .text(createPostRequest.getContentText())
                .post(post)
                .build();
        contentRepository.save(content);

        return DetailPostResponse.toDto(post, content);
    }

    /**
     * 게시글 리스트 조회 (페이징 및 검색)
     */
    @Transactional(readOnly = true)
    public Page<SummaryPostResponse> getPostList(Long boardId, String keyword, Pageable pageable) {
        // 게시판 존재 여부 확인
        boardRepository.findById(boardId)
                .orElseThrow(() -> new NoSuchElementException("게시판이 존재하지 않습니다."));

        // 검색어 조건에 따른 게시글 조회
        Page<Post> postPage = (keyword == null || keyword.trim().isEmpty())
                ? postRepository.findByBoardId(boardId, pageable)
                : postRepository.searchByBoardIdAndKeyword(boardId, keyword, pageable);

        return postPage.map(SummaryPostResponse::toDto);
    }

    /**
     * 게시글 상세 조회
     */
    @Transactional(readOnly = true)
    public DetailPostResponse getPostDetail(Long postId) {
        Post post = getPost(postId);
        Content content = contentRepository.findByPostId(post)
                .orElseThrow(() -> new IllegalArgumentException("게시글의 내용이 존재하지 않습니다."));

        return DetailPostResponse.toDto(post, content);
    }

    /**
     * 게시글 단건 조회 (공통 로직)
     */
    private Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("게시글을 찾을 수 없습니다."));
    }
}
