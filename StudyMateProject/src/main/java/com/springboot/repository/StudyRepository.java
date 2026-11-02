package com.springboot.repository;

import com.springboot.entity.Study;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyRepository extends JpaRepository<Study, Long> {

    // 최신순 목록 (개설자 정보 함께 조회)
    @EntityGraph(attributePaths = "leader")
    List<Study> findAllByOrderByCreatedAtDesc();
}
