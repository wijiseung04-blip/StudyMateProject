package com.springboot.controller;

import com.springboot.config.SessionConst;
import com.springboot.entity.User;
import com.springboot.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    // 로그인 화면
    @GetMapping("/login")
    public String loginForm(@RequestParam(value = "redirectURL", defaultValue = "/studies") String redirectURL,
                            Model model) {
        model.addAttribute("redirectURL", redirectURL);
        return "login";
    }

    // 로그인 처리
    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam(value = "redirectURL", defaultValue = "/studies") String redirectURL,
                        HttpServletRequest request,
                        Model model) {
        try {
            User user = userService.login(username, password);

            HttpSession session = request.getSession();
            session.setAttribute(SessionConst.LOGIN_USER_ID, user.getId());
            session.setAttribute(SessionConst.LOGIN_USER_NAME, user.getName());

            return "redirect:" + safeRedirect(redirectURL);

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("username", username);
            model.addAttribute("redirectURL", redirectURL);
            return "login";
        }
    }

    // 로그아웃
    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/studies";
    }

    // 외부 사이트로의 리다이렉트 방지
    private String safeRedirect(String url) {
        if (url == null || !url.startsWith("/") || url.startsWith("//") || url.contains("\\")) {
            return "/studies";
        }
        return url;
    }
}
