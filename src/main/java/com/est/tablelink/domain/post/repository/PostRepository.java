package com.est.tablelink.domain.post.repository;

import com.est.tablelink.domain.post.domain.Post;
import com.est.tablelink.domain.user.domain.User;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 게시글 작성자 찾기
    List<Post> findAllByAuthor(User user);

    Collection<Object> findByBoardId(Long boardId);
}
