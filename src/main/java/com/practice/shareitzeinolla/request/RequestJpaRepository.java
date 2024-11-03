package com.practice.shareitzeinolla.request;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestJpaRepository extends JpaRepository<Request,Long> {
    List<Request> findAllByUserId(Long userId);
}
