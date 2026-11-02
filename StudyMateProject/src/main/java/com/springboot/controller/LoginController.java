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
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    // 로그인 화면
    @GetMapping("/login")
    public String loginForm(@RequestParam(value = "redirectURL", defaultValue = "/studies") String redirectURL,
                            @SessionAttribute(name = SessionConst.LOGIN_USER_ID, required = false) Long loginUserId,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        // 이미 로그인한 사용자는 로그인 화면 대신 원래 페이지로 이동
        if (loginUserId != null) {
            redirectAttributes.addFlashAttribute("message", "이미 로그인되어 있습니다.");
            return "redirect:" + safeRedirect(redirectURL);
        }

        model.addAttribute("redirectURL", redirectURL);
        return "login";
    }

    // 로그인 처리
    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam(value = "redirectURL", defaultValue = "/studies") String redirectURL,
                        HttpServletRequest request,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        try {
            // 사용자 인증
            User user = userService.login(username, password);

            // 로그인 상태 저장 (세션 고정 공격 방지를 위해 세션 ID 재발급)
            HttpSession session = request.getSession();
            request.changeSessionId();
            session.setAttribute(SessionConst.LOGIN_USER_ID, user.getId());
            session.setAttribute(SessionConst.LOGIN_USER_NAME, user.getName());

            // 로그인 성공 안내
            redirectAttributes.addFlashAttribute("message", user.getName() + "님, 로그인되었습니다.");
            return "redirect:" + safeRedirect(redirectURL);

        } catch (IllegalArgumentException e) {
            // 로그인 실패 → 오류 메시지와 함께 로그인 화면 유지
            model.addAttribute("error", e.getMessage());
            model.addAttribute("username", username);
            model.addAttribute("redirectURL", redirectURL);
            return "login";
        }
    }

    // 로그아웃
    @PostMapping("/logout")
    public String logout(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        redirectAttributes.addFlashAttribute("message", "로그아웃되었습니다.");
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
