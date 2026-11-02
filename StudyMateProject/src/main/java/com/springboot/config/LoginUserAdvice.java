package com.springboot.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * 모든 화면에서 로그인 사용자 이름을 사용할 수 있도록 모델에 추가한다.
 */
@ControllerAdvice
public class LoginUserAdvice {

    @ModelAttribute("loginUserName")
    public String loginUserName(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (String) session.getAttribute(SessionConst.LOGIN_USER_NAME);
    }
}
