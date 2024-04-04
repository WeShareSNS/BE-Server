package com.weshare.api.v1.domain.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    public void 사용자_이름_변경() {
        // given
        String name = "username";
        User user = createUser("test1@test.com", name, "password",
                LocalDate.of(1999, 9, 27), "profile");
        String updateName = "updateName";
        // when
        user.updateName(updateName);
        // then
        assertTrue(updateName.equals(user.getName()));
    }

    @ParameterizedTest(name = "{index}: {0}은 예외가 발생한다.")
    @ValueSource(strings = {"1", "", " ", "qweasdzxcqweasdzxcqweasdzxc"})
    public void 변경할_이름이_형식에_안맞으면_예외발생(String updateName) {
        // given
        String name = "username";
        User user = createUser("test1@test.com", name, "password",
                LocalDate.of(1999, 9, 27), "profile");
        // when // then
        assertThatThrownBy(() -> user.updateName(updateName))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이름의 길이는 2~20 사이어야 합니다.");
    }

    @Test
    public void 사용자_생년월일_변경() {
        // given
        LocalDate birthDate = LocalDate.of(1999, 9, 27);
        User user = createUser("test1@test.com", "test1", "password",
                birthDate, "profile");
        LocalDate updateBirthDate = LocalDate.of(2018, 10, 26);
        // when
        user.updateBirthDate(updateBirthDate);
        // then
        assertTrue(updateBirthDate.equals(user.getBirthDate()));
    }

    @Test
    public void 변경할_생년월일이_형식에_안맞으면_예외발생() {
        // given
        LocalDate birthDate = LocalDate.of(1999, 9, 27);
        User user = createUser("test1@test.com", "test1", "password",
                birthDate, "profile");
        LocalDate exBirthDate = LocalDate.of(9999, 10, 26);
        // when // then
        assertThatThrownBy(() -> user.updateBirthDate(exBirthDate))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("생년월일이 올바르지 않습니다.");
    }

    @Test
    public void 변경할_생년월일_값이_없으면_예외발생() {
        // given
        LocalDate birthDate = LocalDate.of(1999, 9, 27);
        User user = createUser("test1@test.com", "test1", "password",
                birthDate, "profile");
        // when // then
        assertThatThrownBy(() -> user.updateBirthDate(null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("생년월일이 올바르지 않습니다.");
    }

    @ParameterizedTest(name = "{index}: {0}은 프로필 이미지로 변경 가능하다.")
    @ValueSource(strings = {"www.web.site", "https://web.site.sth", "http://web.site", "uhanuu.site", "uhanuu.com"})
    public void 사용자_프로필_변경(String updateProfileImg) {
        // given
        String profileImg = "profile";
        User user = createUser("test1@test.com", "test1", "password",
                LocalDate.of(1999, 9, 27), profileImg);
        // when
        user.updateProfileImg(updateProfileImg);
        // then
        assertTrue(updateProfileImg.equals(user.getProfileImg()));
    }

    @ParameterizedTest(name = "{index}: {0}은 예외가 발생한다.")
    @ValueSource(strings = {"", " ", "website", "http:web.site"})
    public void 변경할_프로필은_형식에_안맞으면_예외발생(String updateProfileImg) {
        // given
        User user = createUser("test1@test.com", "test12", "password",
                LocalDate.of(1999, 9, 27), "profile");
        // when // then
        assertThatThrownBy(() -> user.updateProfileImg(updateProfileImg))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("프로필 이미지 정보가 올바르지 않습니다.");
    }

    @Test
    public void 사용자_비밀번호_변경() {
        // given
        String password = "password";
        User user = createUser("test1@test.com", "test1", password,
                LocalDate.of(1999, 9, 27), "profile");
        String updatePassword = "updatePassword";
        // when
        user.updatePassword(updatePassword, passwordEncoder);
        // then
        assertTrue(passwordEncoder.matches(updatePassword, user.getPassword()));
    }

    @ParameterizedTest(name = "{index}: {0}은 길이가 올바르지 않아서 예외가 발생한다.")
    @ValueSource(strings = {"asd", "qweqwe", "qweqweqwequjwbndjqwbd"})
    public void 변경할_패스워드는_형식에_안맞으면_예외발생(String updatePassword) {
        // given
        User user = createUser("test1@test.com", "test12", "password",
                LocalDate.of(1999, 9, 27), "profile");
        // when // then
        assertThatThrownBy(() -> user.updatePassword(updatePassword, passwordEncoder))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("패스워드의 길이는 8~16 사이어야 합니다.");
    }

    private User createUser(String email, String name, String password, LocalDate birthDate, String profileImg) {
        return User.builder()
                .email(email)
                .name(name)
                .birthDate(birthDate)
                .password(passwordEncoder.encode(password))
                .profileImg(profileImg)
                .build();
    }
}