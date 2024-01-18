package com.weShare.api.v1.auth;

import com.weShare.api.IntegrationTestSupport;
import com.weShare.api.v1.domain.user.entity.User;
import com.weShare.api.v1.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthenticationServiceTest extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationService authService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    void tearDown(){
        userRepository.deleteAllInBatch();
    }

    // dto에 의존하는 test 상관 없으려나,,, test때문에 빌더도 쓰는데
    @Test
    @DisplayName("사용자는 회원가입을 할 수 있다.")
    public void join() {
        // given
        String email = "test@exam.com";
        String password = "password";
        LocalDate birthDate = LocalDate.of(1999, 9, 27);
        JoinRequest request = createJoinRequest(email, password, birthDate);

        // when
        authService.join(request);
        User findUSer = userRepository.findByEmail(email).get();
        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals(findUSer.getEmail(), email),
                () -> Assertions.assertTrue(passwordEncoder.matches(password, findUSer.getPassword())),
                () -> Assertions.assertEquals(findUSer.getBirthDate(), birthDate)
        );
    }

    @Test
    @DisplayName("이미 가입된 이메일인 경우 예외가 발생한다.")
    public void AuthenticationServiceTest() {
        // given
        String email = "test@exam.com";
        String password = "password";
        LocalDate birthDate = LocalDate.of(1999, 9, 27);
        createAndSaveUser(email, password);
        JoinRequest request = createJoinRequest(email, password, birthDate);

        // when //then
        assertThatThrownBy(() -> authService.join(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(String.format("%s은 가입된 이메일 입니다.", email));
    }

    @Test
    @DisplayName("사용자는 로그인할 수 있다.")
    public void login() {
        // given
        String email ="test";
        String password = "pass";
        createAndSaveUser(email, password);
        LoginRequest request = createLoginRequest(email, password);
        // when
        authService.login(request);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // then
        System.out.println(authentication.getAuthorities());
        System.out.println("===================");
        System.out.println(authentication.getDetails());
        System.out.println("===================");
        System.out.println(authentication.getPrincipal());
        System.out.println("===================");
        System.out.println(authentication.getName());
        System.out.println("===================");
        System.out.println(authentication.getCredentials());
//        Assertions.assertAll(
//                () -> Assertions.assertEquals(,
//                () -> Assertions.assertEquals()
//        );

    }

    private User createAndSaveUser(String email, String password) {
        User user = User.builder()
                .email(email)
                .name("not null")
                .password(password)
                .birthDate(LocalDate.of(1999, 9, 27))
                .build();

        return userRepository.save(user);
    }

    private JoinRequest createJoinRequest(String email, String password, LocalDate birthDate) {
        return JoinRequest.builder()
                .email(email)
                .password(password)
                .birthDate(birthDate)
                .build();
    }

    private LoginRequest createLoginRequest(String email, String password) {
        return LoginRequest.builder()
                .email(email)
                .password(password)
                .build();
    }
}