package com.weshare.api.v1.service.user;

import com.weshare.api.IntegrationTestSupport;
import com.weshare.api.v1.domain.user.Role;
import com.weshare.api.v1.domain.user.Social;
import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.repository.user.UserRepository;
import com.weshare.api.v1.service.user.dto.UserUpdateDto;
import com.weshare.api.v1.service.user.dto.PasswordUpdateDto;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserServiceTest extends IntegrationTestSupport {
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
    }

    @TestFactory
    Collection<DynamicTest> 사용자는_정보를_수정할_수_있다() {
        // given
        User user = createAndSaveUser("test1@test.com", "test1", "password");
        return List.of(DynamicTest.dynamicTest("사용자의 닉네임을 변경할 수 있다.", () -> {
                    //given
                    String updateName = "newTest1";
                    UserUpdateDto updateDto = UserUpdateDto.builder()
                            .userEmail(user.getEmail())
                            .name(updateName)
                            .build();
                    //when
                    userService.updateUser(updateDto);
                    //then
                    Optional<User> findUser = userRepository.findByName(updateName);
                    assertTrue(findUser.isPresent());
                }), DynamicTest.dynamicTest("사용자 프로필 정보를 수정할 수 있다.", () -> {
                    //given
                    String updateProfile = "uhanuu.site";
                    UserUpdateDto updateDto = UserUpdateDto.builder()
                            .userEmail(user.getEmail())
                            .profileImg(updateProfile)
                            .build();
                    //when
                    userService.updateUser(updateDto);
                    //then
                    User findUser = userRepository.findByEmail(user.getEmail()).orElseThrow();
                    assertThat(findUser.getProfileImg()).isEqualTo(updateProfile);
                }), DynamicTest.dynamicTest("사용자 생년월일을 수정할 수 있다.", () -> {
                    //given
                    LocalDate updateBirthDate = LocalDate.of(1999, 9, 27);
                    UserUpdateDto updateDto = UserUpdateDto.builder()
                            .userEmail(user.getEmail())
                            .birthDate(updateBirthDate)
                            .build();
                    //when
                    userService.updateUser(updateDto);
                    //then
                    User findUser = userRepository.findByEmail(user.getEmail()).orElseThrow();
                    assertThat(findUser.getBirthDate()).isEqualTo(updateBirthDate);
                }), DynamicTest.dynamicTest("사용자 비밀번호를 수정할 수 있다.", () -> {
                    //given
                    LocalDate updateBirthDate = LocalDate.of(1999, 9, 27);
                    UserUpdateDto updateDto = UserUpdateDto.builder()
                            .userEmail(user.getEmail())
                            .birthDate(updateBirthDate)
                            .build();
                    //when
                    userService.updateUser(updateDto);
                    //then
                    User findUser = userRepository.findByEmail(user.getEmail()).orElseThrow();
                    assertThat(findUser.getBirthDate()).isEqualTo(updateBirthDate);
                })

        );
    }

    @ParameterizedTest(name = "{index}: {0}은 예외가 발생한다.")
    @ValueSource(strings = {"q", "qweasdzxcqweasdzxcqweasdzxc"})
    public void 사용자_닉네임_수정시_길이가_올바르지_않으면_예외가_발생한다(String updateName) {
        // given
        User user = createAndSaveUser("test2@test.com", "test2", "password");
        UserUpdateDto updateDto = UserUpdateDto.builder()
                .userEmail(user.getEmail())
                .name(updateName)
                .build();
        // when
        assertThatThrownBy(() -> userService.updateUser(updateDto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이름의 길이는 2~20 사이어야 합니다.");
    }

    @Test
    public void 사용자는_패스워드를_수정할_수_있다() {
        // given
        String password = "password";
        User user = createAndSaveUser("test3@test.com", "test3", password);

        String newPassword = "newPassword";
        PasswordUpdateDto passwordUpdateDto = createPasswordUpdateDto(user, password, newPassword, newPassword);
        userService.updatePassword(passwordUpdateDto);
        // when
        User findUser = userRepository.findByEmail(user.getEmail()).orElseThrow();
        // then
        assertTrue(passwordEncoder.matches(newPassword, findUser.getPassword()));
    }

    @Test
    public void 비밀번호_재입력이_일치하지_않으면_예외가_발생한다() {
        // given
        String password = "password";
        User user = createAndSaveUser("test3@test.com", "test3", password);

        String newPassword = "newPassword";
        String verifyPassword = "새로운비밀번호와다름";
        PasswordUpdateDto passwordUpdateDto = createPasswordUpdateDto(user, password, newPassword, verifyPassword);

        // when // then
        assertThatThrownBy(() -> userService.updatePassword(passwordUpdateDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("패스워드 재입력이 올바르지 않습니다.");
    }

    @Test
    public void 기존_패스워드가_일치하지_않으면_예외가_발생한다() {
        // given
        String password = "password";
        User user = createAndSaveUser("test3@test.com", "test3", password);

        String notSameOldPassword = "qweasdzxc";
        String newPassword = "newPassword";
        PasswordUpdateDto passwordUpdateDto = createPasswordUpdateDto(user, notSameOldPassword, newPassword, newPassword);

        // when // then
        assertThatThrownBy(() -> userService.updatePassword(passwordUpdateDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("기존 패스워드가 올바르지 않습니다.");
    }

    private PasswordUpdateDto createPasswordUpdateDto(User user, String password, String newPassword, String verifyPassword) {
        return PasswordUpdateDto.builder()
                .userEmail(user.getEmail())
                .oldPassword(password)
                .newPassword(newPassword)
                .verifyPassword(verifyPassword)
                .build();
    }

    private User createAndSaveUser(String email, String name, String password) {
        User user = User.builder()
                .email(email)
                .name(name)
                .password(passwordEncoder.encode(password))
                .profileImg("profile")
                .role(Role.USER)
                .social(Social.DEFAULT)
                .build();
        return userRepository.save(user);
    }

}