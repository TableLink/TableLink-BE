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
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/board")
@CrossOrigin(origins = "https://localhost:8081")
@AllArgsConstructor
@Tag(name = "Board Controller", description = "게시판 관련 API")
public class BoardController {

    private final BoardService boardService;

    // 게시판 생성
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "게시판 생성", description = "인증된 사용자가 게시판을 생성할 때 사용하는 API")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "게시판 생성 성공"
                    , content = @Content(schema = @Schema(implementation = BoardResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못 요청된 데이터"
                    , content = @Content(schema = @Schema(implementation = BoardResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "중복된 게시판 이름"
                    , content = @Content(schema = @Schema(implementation = BoardResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "관리자가 아닌 사용자의 요청"
                    , content = @Content(schema = @Schema(implementation = BoardResponse.class))),
    })
    public ResponseEntity<ApiResponse<BoardResponse>> createBoard(
            @Valid @RequestBody CreateBoardRequest createBoardRequest) {

        // 중복된 게시판 이름 체크
        if (boardService.isBoardNameDuplicate(createBoardRequest.getBoardName())) {
            ApiResponse<BoardResponse> conflictResponse = ApiResponse.<BoardResponse>builder()
                    .result(null)
                    .resultCode(HttpStatus.CONFLICT.value())
                    .resultMsg("중복된 이름의 게시판이 존재합니다.")
                    .build();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(conflictResponse);
        }
        // 게시판 생성
        Board board = boardService.createBoard(createBoardRequest);
        BoardResponse boardResponse = BoardResponse.toDto(board);
        ApiResponse<BoardResponse> successResponse = ApiResponse.<BoardResponse>builder()
                .result(boardResponse)
                .resultCode(HttpStatus.CREATED.value())
                .resultMsg("게시판 생성 성공")
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(successResponse);
    }

    // 게시판 삭제
    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "게시판 삭제", description = "인증된 사용자가 기존에 생성 되어있는 게시판을 삭제 합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시판 삭제 성공"
                    , content = @Content(schema = @Schema(implementation = BoardResponse.class)))
    })
    public ResponseEntity<ApiResponse<String>> deleteBoard(@Valid Long id) {
        boardService.deleteBoard(id);
        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .result("게시판 삭제 성공")
                .resultCode(HttpStatus.OK.value())
                .resultMsg("게시판 삭제 성공")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    // 게시판 리스트
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<BoardResponse>>> getBoardList(){
        List<Board> boardList = boardService.getBoardList();
        List<BoardResponse> boardResponseList = boardList.stream()
                .map(BoardResponse::toDto)
                .toList();

        ApiResponse<List<BoardResponse>> apiResponse = ApiResponse.<List<BoardResponse>>builder()
                .result(boardResponseList)
                .resultCode(HttpStatus.OK.value())
                .resultMsg("게시판 리스트 불러오기 성공")
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
