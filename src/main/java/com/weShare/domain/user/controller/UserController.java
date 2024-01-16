package com.weShare.domain.user.controller;

import com.weShare.domain.user.entity.User;
import com.weShare.domain.user.entity.UserRole;
import com.weShare.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @PostMapping
    public User createUser() {
        User user = User.builder()
                .email("admin@exam.com")
                .birthDate(LocalDate.of(1999, 9, 27))
                .profileImg(null)
                .password("test")
                .username("hihi")
                .roles(List.of(UserRole.ROLE_USER))
                .build();
        return userRepository.save(user);
    }

    @GetMapping
    public List<User> getUser() {
        return userRepository.findAll();
    }
}
