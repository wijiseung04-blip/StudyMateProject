package com.springboot.service;

import com.springboot.entity.User;
import com.springboot.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserServiceLoginTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);
        when(userRepository.findByUsername("user1"))
                .thenReturn(Optional.of(new User("user1", "pw1234", "홍길동", "a@a.com")));
    }

    @Test
    void 등록된_사용자는_로그인할_수_있다() {
        User user = userService.login("user1", "pw1234");
        assertThat(user.getName()).isEqualTo("홍길동");
    }

    @Test
    void 비밀번호가_틀리면_로그인할_수_없다() {
        assertThatThrownBy(() -> userService.login("user1", "wrong"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("아이디 또는 비밀번호가 올바르지 않습니다.");
    }

    @Test
    void 존재하지_않는_아이디는_로그인할_수_없다() {
        when(userRepository.findByUsername("nobody")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.login("nobody", "pw1234"))
                .hasMessage("아이디 또는 비밀번호가 올바르지 않습니다.");
    }

    @Test
    void 아이디나_비밀번호가_비어있으면_로그인할_수_없다() {
        assertThatThrownBy(() -> userService.login(" ", "pw1234"))
                .hasMessage("아이디와 비밀번호를 입력해주세요.");
        assertThatThrownBy(() -> userService.login("user1", ""))
                .hasMessage("아이디와 비밀번호를 입력해주세요.");
    }
}
