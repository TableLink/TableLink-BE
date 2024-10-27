package com.est.tablelink.domain.board.controller;

import com.est.tablelink.domain.board.domain.Board;
import com.est.tablelink.domain.board.dto.request.CreateBoardRequest;
import com.est.tablelink.domain.board.dto.response.BoardResponse;
import com.est.tablelink.domain.board.service.BoardService;
import com.est.tablelink.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/board")
@CrossOrigin(origins = "http://localhost:8081")
@AllArgsConstructor
@Tag(name = "Board Controller", description = "게시판 관련 API")
public class BoardController {

    private final BoardService boardService;

    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "게시판 생성", description = "인증된 사용자가 게시판을 생성할 때 사용하는 API")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "게시판 생성 성공"
            ,content = @Content(schema = @Schema(implementation = BoardResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못 요청된 데이터"
            ,content = @Content(schema = @Schema(implementation = BoardResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "중복된 게시판 이름"
            ,content = @Content(schema = @Schema(implementation = BoardResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "인증되지 않은 사용자"
            ,content = @Content(schema = @Schema(implementation = BoardResponse.class))),
    })
    public ResponseEntity<ApiResponse<BoardResponse>> createBoard(
            @RequestBody CreateBoardRequest createBoardRequest) {

        ResponseEntity<ApiResponse<BoardResponse>> result;
        // 중복된 게시판 이름 체크
        if (!boardService.isBoardNameDuplicate(createBoardRequest.getBoardName())) {
            ApiResponse<BoardResponse> apiResponse = ApiResponse.<BoardResponse>builder()
                    .result(null)
                    .resultCode(HttpStatus.CONFLICT.value())
                    .resultMsg("중복된 이름의 게시판이 존재합니다.")
                    .build();
            result = ResponseEntity.status(HttpStatus.CONFLICT).body(apiResponse);
        } else {
            // 게시판 생성
            Board board = boardService.createBoard(createBoardRequest);
            BoardResponse boardResponse = BoardResponse.toDto(board);
            ApiResponse<BoardResponse> apiResponse = ApiResponse.<BoardResponse>builder()
                    .result(boardResponse)
                    .resultCode(HttpStatus.CREATED.value())
                    .resultMsg("게시판 생성 성공")
                    .build();
            result = ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
        }
        return result;
    }
}
