package com.est.tablelink.domain.user.repository;

import com.est.tablelink.domain.user.domain.User;
import com.est.tablelink.domain.user.util.Role;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByNickname(String nickname);

    @Query("SELECT u FROM User u WHERE u.role = :role AND "
            + "(u.nickname LIKE %:keyword% OR u.username LIKE %:keyword%)")
    Page<User> findByRoleAndKeyword(@Param("role") Role role, @Param("keyword") String keyword, Pageable pageable);

    Page<User> findByRole(Role role, Pageable pageable);
}
