package com.est.tablelink.domain.board.service;

import com.est.tablelink.domain.board.domain.Board;
import com.est.tablelink.domain.board.dto.request.CreateBoardRequest;
import com.est.tablelink.domain.board.repository.BoardRepository;
import com.est.tablelink.domain.user.domain.User;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    // 게시글 생성 로직
    @Transactional
    public Board createBoard(CreateBoardRequest createBoardRequest) {
        Board board = createBoardRequest.toEntity();
        return boardRepository.save(board);
    }

    @Transactional(readOnly = true)
    public Boolean isBoardNameDuplicate(String boardName) {
        Optional<Board> board = boardRepository.findByBoardName(boardName);
        return board.isEmpty();
    }
}
