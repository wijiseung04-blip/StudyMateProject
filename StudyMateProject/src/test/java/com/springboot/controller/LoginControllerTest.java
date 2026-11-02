package com.springboot.controller;

import com.springboot.config.SessionConst;
import com.springboot.entity.User;
import com.springboot.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class LoginControllerTest {

    private UserService userService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        // 단위 테스트이므로 실제 템플릿 대신 단순 뷰 리졸버 사용
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver("/templates/", ".html");
        mockMvc = MockMvcBuilders.standaloneSetup(new LoginController(userService))
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    void 로그인_성공시_세션에_저장되고_원래_페이지로_이동한다() throws Exception {
        User user = new User("user1", "pw1234", "홍길동", "a@a.com");
        ReflectionTestUtils.setField(user, "id", 1L);
        when(userService.login("user1", "pw1234")).thenReturn(user);

        MvcResult result = mockMvc.perform(post("/login")
                        .param("username", "user1")
                        .param("password", "pw1234")
                        .param("redirectURL", "/studies/new"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/studies/new"))
                .andExpect(flash().attribute("message", "홍길동님, 로그인되었습니다."))
                .andReturn();

        MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);
        assertThat(session).isNotNull();
        assertThat(session.getAttribute(SessionConst.LOGIN_USER_ID)).isEqualTo(1L);
        assertThat(session.getAttribute(SessionConst.LOGIN_USER_NAME)).isEqualTo("홍길동");
    }

    @Test
    void 로그인_실패시_오류메시지와_함께_로그인화면을_보여준다() throws Exception {
        when(userService.login("user1", "wrong"))
                .thenThrow(new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다."));

        mockMvc.perform(post("/login")
                        .param("username", "user1")
                        .param("password", "wrong"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attribute("error", "아이디 또는 비밀번호가 올바르지 않습니다."))
                .andExpect(model().attribute("username", "user1"));
    }

    @Test
    void 외부_주소로는_리다이렉트하지_않는다() throws Exception {
        User user = new User("user1", "pw1234", "홍길동", "a@a.com");
        ReflectionTestUtils.setField(user, "id", 1L);
        when(userService.login("user1", "pw1234")).thenReturn(user);

        mockMvc.perform(post("/login")
                        .param("username", "user1")
                        .param("password", "pw1234")
                        .param("redirectURL", "//evil.com"))
                .andExpect(redirectedUrl("/studies"));
    }

    @Test
    void 이미_로그인한_사용자는_로그인화면_대신_이동한다() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_USER_ID, 1L);

        mockMvc.perform(get("/login").session(session))
                .andExpect(redirectedUrl("/studies"));
    }

    @Test
    void 로그아웃하면_세션이_만료된다() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConst.LOGIN_USER_ID, 1L);

        mockMvc.perform(post("/logout").session(session))
                .andExpect(redirectedUrl("/studies"))
                .andExpect(flash().attribute("message", "로그아웃되었습니다."));

        assertThat(session.isInvalid()).isTrue();
    }
}
