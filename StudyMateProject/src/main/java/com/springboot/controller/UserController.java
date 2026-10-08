package com.springboot.controller;

import com.springboot.entity.User;
import com.springboot.service.UserService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 회원가입 화면
    @GetMapping("/signup")
    public String signupForm() {
        return "signup";
    }

    // 회원가입 요청 처리
    @PostMapping("/signup")
    public String signup(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            Model model) {

        try {

            User user = userService.signup(
                    username,
                    password,
                    name,
                    email
            );

            model.addAttribute("user", user);

            return "signup-success";

        } catch (IllegalArgumentException e) {

            model.addAttribute("error", e.getMessage());

            return "signup";
        }
    }
}