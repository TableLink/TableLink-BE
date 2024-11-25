package com.est.tablelink.domain.post.repository;

import com.est.tablelink.domain.post.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByBoardId(Long boardId, Pageable pageable);

    @Query("SELECT p FROM Post p "
            + "JOIN Content c ON p.id = c.post.id WHERE p.board.id = :boardId "
            + "AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) ")
    Page<Post> searchByBoardIdAndKeyword(@Param("boardId") Long boardId,
            @Param("keyword") String keyword, Pageable pageable);
}
