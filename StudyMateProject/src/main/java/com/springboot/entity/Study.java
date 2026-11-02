package com.springboot.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "studies")
public class Study {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 스터디 제목
    @Column(nullable = false, length = 100)
    private String title;

    // 스터디 설명
    @Column(nullable = false, length = 1000)
    private String description;

    // 모집 인원
    @Column(nullable = false)
    private int maxMembers;

    // 스터디 개설자 (로그인한 사용자)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "leader_id", nullable = false)
    private User leader;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected Study() {
    }

    public Study(String title, String description, int maxMembers, User leader) {
        this.title = title;
        this.description = description;
        this.maxMembers = maxMembers;
        this.leader = leader;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // 스터디 정보 수정
    public void update(String title, String description, int maxMembers) {
        this.title = title;
        this.description = description;
        this.maxMembers = maxMembers;
    }

    // 개설자 본인인지 확인
    public boolean isLeader(Long userId) {
        return userId != null && leader.getId().equals(userId);
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public User getLeader() {
        return leader;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
