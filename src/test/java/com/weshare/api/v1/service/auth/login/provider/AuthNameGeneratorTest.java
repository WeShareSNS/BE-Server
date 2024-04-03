package com.weshare.api.v1.service.auth.login.provider;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

class AuthNameGeneratorTest {

    @Test
    public void 이메일을_가지고_닉네임을_생성할_수_있다() {
        // given
        String email = "test@test.com";
        // when
        String name = AuthNameGenerator.generateNameToEmail(email);
        // then
        assertThat(name).isEqualTo("test");
    }

    @Test
    public void 이름을_가지고_무작위_이름을_생성할_수_있다() {
        // given
        String expectedValue = "1111";
        String name = "ased";
        // when // then
        try (MockedStatic<AuthNameGenerator.Randoms> generate = mockStatic(AuthNameGenerator.Randoms.class)) {
            generate.when(() -> AuthNameGenerator.Randoms.getRandomDigits(4)).thenReturn(expectedValue);
            String uniqueName = AuthNameGenerator.generateUniqueNameRandomized(name);
            assertThat(uniqueName).isEqualTo(name + "#" + expectedValue);
        }
    }

    @Test
    public void 새로운_무작위_이름을_생성할_수_있다() {
        // given
        String expectedValue = "1111";
        String name = "ased";
        String uniqueName = name + "#1234";
        // when // then
        try (MockedStatic<AuthNameGenerator.Randoms> generate = mockStatic(AuthNameGenerator.Randoms.class)) {
            generate.when(() -> AuthNameGenerator.Randoms.getRandomDigits(4)).thenReturn(expectedValue);
            String newUniqueName = AuthNameGenerator.generateUniqueNameRandomized(uniqueName);
            assertThat(newUniqueName).isEqualTo(name + "#" + expectedValue);
        }
    }
}