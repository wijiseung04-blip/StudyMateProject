package com.springboot.repository;

import com.springboot.entity.Study;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface StudyRepository extends JpaRepository<Study, Long> {

    // 최신순 목록 (개설자 정보 함께 조회)
    @EntityGraph(attributePaths = "leader")
    List<Study> findAllByOrderByCreatedAtDesc();
    
    @Query("""
            select s from Study s join fetch s.leader
            where s.maxMembers between :min and :max
              and (
                    (:type <> 'description' and lower(s.title) like lower(concat('%', :keyword, '%')))
                 or (:type <> 'title' and lower(s.description) like lower(concat('%', :keyword, '%')))
              )
            order by s.createdAt desc
            """)
    List<Study> search(@Param("keyword") String keyword,
                       @Param("type") String type,
                       @Param("min") int min,
                       @Param("max") int max);
}
