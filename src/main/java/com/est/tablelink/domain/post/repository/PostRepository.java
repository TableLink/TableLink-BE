package com.est.tablelink.domain.post.repository;

import com.est.tablelink.domain.post.domain.Post;
import com.est.tablelink.domain.user.domain.User;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByBoardId(Long boardId);
}
