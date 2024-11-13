package com.est.tablelink.domain.board.service;

import com.est.tablelink.domain.board.domain.Board;
import com.est.tablelink.domain.board.dto.request.CreateBoardRequest;
import com.est.tablelink.domain.board.repository.BoardRepository;
import com.est.tablelink.domain.user.domain.User;
import java.util.List;
import java.util.NoSuchElementException;
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

    // 게시판 명 중복 확인
    @Transactional(readOnly = true)
    public Boolean isBoardNameDuplicate(String boardName) {
        Optional<Board> board = boardRepository.findByBoardName(boardName);
        return board.isPresent();
    }

    // 게시판 삭제
    public void deleteBoard(Long id) {
        Board board = getBoard(id);

        boardRepository.delete(board);
    }

    // 게시판 조회
    private Board getBoard(Long id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("요청한 데이터가 존재하지 않습니다."));
    }

    @Transactional(readOnly = true)
    public List<Board> getBoardList() {
        return boardRepository.findAll();
    }
}
