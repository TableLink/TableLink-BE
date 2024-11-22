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
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/post")
@CrossOrigin(origins = "http://localhost:8081")
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
            @RequestBody CreatePostRequest createPostRequest) {
        DetailPostResponse detailPostResponse = postService.createPost(createPostRequest);
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
    public ResponseEntity<ApiResponse<List<SummaryPostResponse>>> getPostList(
            @PathVariable Long boardId) {
        List<SummaryPostResponse> summaryPostResponseList = postService.getPostList(boardId);
        ApiResponse<List<SummaryPostResponse>> successApi = ApiResponse.<List<SummaryPostResponse>>builder()
                .result(summaryPostResponseList)
                .resultCode(HttpStatus.OK.value())
                .resultMsg("게시글 리스트 불러오기 성공")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(successApi);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<DetailPostResponse>> getPostDetail(
            @PathVariable Long id) {
        DetailPostResponse detailPostResponse = postService.getPostDetail(id);
        ApiResponse<DetailPostResponse> successApi = ApiResponse.<DetailPostResponse>builder()
                .result(detailPostResponse)
                .resultCode(HttpStatus.OK.value())
                .resultMsg("게시글 상세 불러오기 성공")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(successApi);
    }
}
