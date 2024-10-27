package com.est.tablelink.domain.board.dto.request;

import com.est.tablelink.domain.board.domain.Board;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateBoardRequest {

    private String boardName;

    @JsonCreator
    public CreateBoardRequest(@JsonProperty("boardName")String boardName) {
        this.boardName = boardName;
    }

    public Board toEntity() {
        return Board.builder()
                .boardName(boardName)
                .build();
    }
}
