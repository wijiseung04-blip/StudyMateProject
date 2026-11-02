package com.springboot.service;

import com.springboot.dto.StudyForm;
import com.springboot.entity.Study;
import com.springboot.entity.User;
import com.springboot.repository.StudyRepository;
import com.springboot.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StudyServiceTest {

    private StudyRepository studyRepository;
    private UserRepository userRepository;
    private StudyService studyService;

    private User leader;
    private User other;

    @BeforeEach
    void setUp() {
        studyRepository = mock(StudyRepository.class);
        userRepository = mock(UserRepository.class);
        studyService = new StudyService(studyRepository, userRepository);

        leader = new User("leader", "pw", "리더", "leader@test.com");
        ReflectionTestUtils.setField(leader, "id", 1L);
        other = new User("other", "pw", "다른사람", "other@test.com");
        ReflectionTestUtils.setField(other, "id", 2L);
    }

    private StudyForm form(String title, String description, Integer maxMembers) {
        StudyForm form = new StudyForm();
        form.setTitle(title);
        form.setDescription(description);
        form.setMaxMembers(maxMembers);
        return form;
    }

    @Test
    void 로그인한_사용자는_스터디를_생성할_수_있다() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(leader));
        when(studyRepository.save(any(Study.class))).thenAnswer(inv -> inv.getArgument(0));

        Study study = studyService.create(1L, form("  자바 스터디 ", "매주 토요일", 5));

        assertThat(study.getTitle()).isEqualTo("자바 스터디");
        assertThat(study.getDescription()).isEqualTo("매주 토요일");
        assertThat(study.getMaxMembers()).isEqualTo(5);
        assertThat(study.getLeader()).isSameAs(leader);
        verify(studyRepository).save(any(Study.class));
    }

    @Test
    void 필수값이_없으면_생성할_수_없다() {
        assertThatThrownBy(() -> studyService.create(1L, form(" ", "설명", 5)))
                .hasMessage("스터디 제목을 입력해주세요.");
        assertThatThrownBy(() -> studyService.create(1L, form("제목", null, 5)))
                .hasMessage("스터디 설명을 입력해주세요.");
        assertThatThrownBy(() -> studyService.create(1L, form("제목", "설명", null)))
                .hasMessage("모집 인원을 입력해주세요.");
        assertThatThrownBy(() -> studyService.create(1L, form("제목", "설명", 1)))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> studyService.create(1L, form("제목", "설명", 51)))
                .isInstanceOf(IllegalArgumentException.class);
        verify(studyRepository, never()).save(any());
    }

    @Test
    void 개설자는_스터디를_수정할_수_있다() {
        Study study = new Study("제목", "설명", 4, leader);
        when(studyRepository.findById(10L)).thenReturn(Optional.of(study));

        studyService.update(10L, 1L, form("새 제목", "새 설명", 8));

        assertThat(study.getTitle()).isEqualTo("새 제목");
        assertThat(study.getDescription()).isEqualTo("새 설명");
        assertThat(study.getMaxMembers()).isEqualTo(8);
    }

    @Test
    void 개설자가_아니면_수정_삭제할_수_없다() {
        Study study = new Study("제목", "설명", 4, leader);
        when(studyRepository.findById(10L)).thenReturn(Optional.of(study));

        assertThatThrownBy(() -> studyService.update(10L, other.getId(), form("a", "b", 3)))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> studyService.delete(10L, other.getId()))
                .isInstanceOf(IllegalStateException.class);
        verify(studyRepository, never()).delete(any());
    }

    @Test
    void 개설자는_스터디를_삭제할_수_있다() {
        Study study = new Study("제목", "설명", 4, leader);
        when(studyRepository.findById(10L)).thenReturn(Optional.of(study));

        studyService.delete(10L, 1L);

        verify(studyRepository).delete(study);
    }
}
