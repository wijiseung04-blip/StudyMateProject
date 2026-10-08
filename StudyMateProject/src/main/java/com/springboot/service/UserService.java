package com.springboot.service;

import com.springboot.entity.User;
import com.springboot.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User signup(String username, String password,
                       String name, String email) {

        // 입력값 검증
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("아이디를 입력해주세요.");
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("비밀번호를 입력해주세요.");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("이름을 입력해주세요.");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("이메일을 입력해주세요.");
        }

        // 아이디 중복 검사
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        // 이메일 중복 검사
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 회원 생성
        User user = new User(
                username,
                password,
                name,
                email
        );

        // DB 저장
        return userRepository.save(user);
    }
}