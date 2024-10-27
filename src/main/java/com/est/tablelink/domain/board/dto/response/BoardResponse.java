package com.est.tablelink.domain.board.dto.response;

import com.est.tablelink.domain.board.domain.Board;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BoardResponse {

    private Long id;
    private String BoardName;

    public static BoardResponse toDto(Board board) {
        return BoardResponse.builder()
                .id(board.getId())
                .BoardName(board.getBoardName())
                .build();
    }
}
