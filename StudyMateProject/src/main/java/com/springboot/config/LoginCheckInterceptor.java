package com.springboot.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 로그인하지 않은 사용자가 스터디 생성/수정/삭제에 접근하면 로그인 화면으로 보낸다.
 */
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute(SessionConst.LOGIN_USER_ID) == null) {
            String redirect = "GET".equalsIgnoreCase(request.getMethod())
                    ? request.getRequestURI()
                    : "/studies";
            response.sendRedirect("/login?redirectURL="
                    + URLEncoder.encode(redirect, StandardCharsets.UTF_8));
            return false;
        }

        return true;
    }
}
