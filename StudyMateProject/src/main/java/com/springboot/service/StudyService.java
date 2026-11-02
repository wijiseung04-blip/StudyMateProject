package com.springboot.service;

import com.springboot.dto.StudyForm;
import com.springboot.entity.Study;
import com.springboot.entity.User;
import com.springboot.repository.StudyRepository;
import com.springboot.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class StudyService {

    public static final int TITLE_MAX_LENGTH = 100;
    public static final int DESCRIPTION_MAX_LENGTH = 1000;
    public static final int MIN_MEMBERS = 2;
    public static final int MAX_MEMBERS = 50;

    private final StudyRepository studyRepository;
    private final UserRepository userRepository;

    public StudyService(StudyRepository studyRepository, UserRepository userRepository) {
        this.studyRepository = studyRepository;
        this.userRepository = userRepository;
    }

    // 스터디 목록 (최신순)
    public List<Study> findAll() {
        return studyRepository.findAllByOrderByCreatedAtDesc();
    }

    // 스터디 단건 조회
    public Study findById(Long studyId) {
        return studyRepository.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스터디입니다."));
    }

    // 스터디 생성
    @Transactional
    public Study create(Long loginUserId, StudyForm form) {
        validate(form);

        User leader = userRepository.findById(loginUserId)
                .orElseThrow(() -> new IllegalStateException("로그인 정보가 올바르지 않습니다."));

        Study study = new Study(
                form.getTitle().trim(),
                form.getDescription().trim(),
                form.getMaxMembers(),
                leader
        );

        // DB 저장
        return studyRepository.save(study);
    }

    // 스터디 수정 (개설자만 가능)
    @Transactional
    public Study update(Long studyId, Long loginUserId, StudyForm form) {
        Study study = findOwnedStudy(studyId, loginUserId);
        validate(form);

        study.update(
                form.getTitle().trim(),
                form.getDescription().trim(),
                form.getMaxMembers()
        );

        return study;
    }

    // 스터디 삭제 (개설자만 가능)
    @Transactional
    public void delete(Long studyId, Long loginUserId) {
        Study study = findOwnedStudy(studyId, loginUserId);
        studyRepository.delete(study);
    }

    // 수정/삭제 권한이 있는 스터디 조회
    public Study findOwnedStudy(Long studyId, Long loginUserId) {
        Study study = findById(studyId);

        if (!study.isLeader(loginUserId)) {
            throw new IllegalStateException("스터디 개설자만 수정/삭제할 수 있습니다.");
        }

        return study;
    }

    // 필수 입력값 검증
    private void validate(StudyForm form) {
        if (form.getTitle() == null || form.getTitle().isBlank()) {
            throw new IllegalArgumentException("스터디 제목을 입력해주세요.");
        }

        if (form.getTitle().trim().length() > TITLE_MAX_LENGTH) {
            throw new IllegalArgumentException("스터디 제목은 " + TITLE_MAX_LENGTH + "자 이하로 입력해주세요.");
        }

        if (form.getDescription() == null || form.getDescription().isBlank()) {
            throw new IllegalArgumentException("스터디 설명을 입력해주세요.");
        }

        if (form.getDescription().trim().length() > DESCRIPTION_MAX_LENGTH) {
            throw new IllegalArgumentException("스터디 설명은 " + DESCRIPTION_MAX_LENGTH + "자 이하로 입력해주세요.");
        }

        if (form.getMaxMembers() == null) {
            throw new IllegalArgumentException("모집 인원을 입력해주세요.");
        }

        if (form.getMaxMembers() < MIN_MEMBERS || form.getMaxMembers() > MAX_MEMBERS) {
            throw new IllegalArgumentException(
                    "모집 인원은 " + MIN_MEMBERS + "명 이상 " + MAX_MEMBERS + "명 이하로 설정해주세요.");
        }
    }
}
