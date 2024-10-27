package com.est.tablelink.domain.board.repository;

import com.est.tablelink.domain.board.domain.Board;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {

    Optional<Board> findByBoardName(String boardName);
}
