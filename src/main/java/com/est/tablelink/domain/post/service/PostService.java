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
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
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

    // 게시글 생성 메서드
    @Transactional
    public DetailPostResponse createPost(CreatePostRequest createPostRequest) {

        CustomUserDetails userDetails = (CustomUserDetails) userService.getAuthentication()
                .getPrincipal();
        Long user_id = userDetails.getId();

        User user = userRepository.findById(user_id)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 입니다."));
        Board board = boardRepository.findById(createPostRequest.getBoardId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 게시판 입니다."));

        Post post = createPostRequest.toEntity(user, board);
        postRepository.save(post);

            Content content = Content.builder()
                    .data(createPostRequest.getContentData())
                    .post(post)
                    .build();
            contentRepository.save(content);

        return DetailPostResponse.toDto(post,content);
    }

    // 게시글 리스트 조회 불러오기 메서드
    @Transactional(readOnly = true)
    public List<SummaryPostResponse> getPostList(Long boardId) {

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new NoSuchElementException("게시판이 존재하지 않습니다."));

        List<Post> postList = postRepository.findByBoardId(board.getId());

        return postList.stream()
                .map(SummaryPostResponse::toDto)
                .collect(Collectors.toList());
    }

    // 게시글 상세 불러오기
    @Transactional(readOnly = true)
    public DetailPostResponse getPostDetail(Long id, Content content) {
        Post post = getPost(id);
        return DetailPostResponse.toDto(post, content);
    }

    private Post getPost(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("게시글을 찾을 수 없습니다."));
    }
}
