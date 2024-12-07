package com.est.tablelink.domain.post.repository;

import com.est.tablelink.domain.post.domain.Content;
import com.est.tablelink.domain.post.domain.Post;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<Content, Long> {

    boolean existsByPost(Post post);

    Optional<Content> findByPostId(Post post);
}
