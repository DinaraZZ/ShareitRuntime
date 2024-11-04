package com.practice.shareitzeinolla.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface RequestJpaRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByUserIdOrderByCreatedDesc(Long userId);

    @Query(value = """
            select r from Request r
            where r.user.id <> :userId
            """)
    List<Request> findAllExceptUserId(@Param("userId") Long userId, Pageable pageable);
}
