package com.weshare.api.v1.service.user;

import com.weshare.api.IntegrationTestSupport;
import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.repository.user.UserRepository;
import com.weshare.api.v1.service.user.dto.UserUpdateDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

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

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
    }

    @TestFactory
    Collection<DynamicTest> 사용자는_정보를_수정할_수_있다() {
        // given
        User user = createAndSaveUser("test1@test.com", "test1");
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
                    String updateProfile = "newProfile";
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
                })
        );
    }

    @ParameterizedTest(name = "{index}: {0}은 예외가 발생한다.")
    @ValueSource(strings = {"q", "qweasdzxcqweasdzxcqweasdzxc"})
    public void 사용자_닉네임_수정시_길이가_올바르지_않으면_예외가_발생한다(String updateName) {
        // given
        User user = createAndSaveUser("test2@test.com", "test2");
        UserUpdateDto updateDto = UserUpdateDto.builder()
                .userEmail(user.getEmail())
                .name(updateName)
                .build();
        // when
        assertThatThrownBy(() -> userService.updateUser(updateDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이름의 길이는 2~20 사이어야 합니다.");
    }

    private User createAndSaveUser(String email, String name) {
        User user = User.builder()
                .email(email)
                .name(name)
                .password("test")
                .profileImg("profile")
                .build();
        return userRepository.save(user);
    }

}