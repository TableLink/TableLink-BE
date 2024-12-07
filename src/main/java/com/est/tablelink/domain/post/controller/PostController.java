package com.est.tablelink.domain.post.controller;

import com.est.tablelink.domain.post.dto.request.CreatePostRequest;
import com.est.tablelink.domain.post.dto.response.DetailPostResponse;
import com.est.tablelink.domain.post.dto.response.SummaryPostResponse;
import com.est.tablelink.domain.post.service.PostService;
import com.est.tablelink.global.common.ApiResponse;
import com.est.tablelink.global.common.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/post")
@CrossOrigin(origins = "https://localhost:8081")
@Tag(name = "Post Controller", description = "게시글 관련 API")
public class PostController {

    private final PostService postService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "게시글 생성", description = "인증된 사용자가 게시판을 생성할 때 사용하는 API")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "게시글 생성 성공"
                    , content = @Content(schema = @Schema(implementation = DetailPostResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"
                    , content = @Content(schema = @Schema(implementation = ErrorResponse.class)))

    })
    public ResponseEntity<ApiResponse<DetailPostResponse>> createPost(
            @RequestBody @Valid CreatePostRequest createPostRequest,
            @RequestParam Long boardId) {
        log.info("요청 데이터: {}", createPostRequest);
        DetailPostResponse detailPostResponse = postService.createPost(createPostRequest, boardId);
        ApiResponse<DetailPostResponse> successApi = ApiResponse.<DetailPostResponse>builder()
                .result(detailPostResponse)
                .resultCode(HttpStatus.CREATED.value())
                .resultMsg("게시글 생성 성공")
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(successApi);
    }

    @GetMapping("/{boardId}/list")
    @Operation(summary = "게시글 목록 조회", description = "게시판 ID로 게시글 목록을 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 조회 성공"
                    , content = @Content(schema = @Schema(implementation = SummaryPostResponse.class)))
    })
    public ResponseEntity<ApiResponse<Page<SummaryPostResponse>>> getPostList(
            @PathVariable Long boardId,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<SummaryPostResponse> summaryPostResponseList = postService.getPostList(boardId, keyword, pageable);
        ApiResponse<Page<SummaryPostResponse>> successApi = ApiResponse.<Page<SummaryPostResponse>>builder()
                .result(summaryPostResponseList)
                .resultCode(HttpStatus.OK.value())
                .resultMsg("게시글 리스트 불러오기 성공")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(successApi);
    }
}
