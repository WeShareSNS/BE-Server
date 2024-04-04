package com.weshare.api.v1.service.auth.login.provider;

import com.weshare.api.IntegrationTestSupport;
import com.weshare.api.v1.domain.user.Role;
import com.weshare.api.v1.domain.user.Social;
import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.repository.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

class ExternalAuthProviderLoginAndJoinHelperTest extends IntegrationTestSupport {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ExternalAuthProviderLoginAndJoinHelper helper;

    @AfterEach
    void tearDown(){
        userRepository.deleteAllInBatch();
    }

    @Test
    public void 사용자_이메일_중복시_재시도가_수행된다() {
        // given
        String name = "test";
        User user = createUser(name + "@test.com", name);
        userRepository.save(user);
        String expectValue = "1111";
        // when
        String uniqueEmail = "unique@email.com";
        User authUser = createUser(uniqueEmail, name);
        try (MockedStatic<AuthNameGenerator.Randoms> generate = mockStatic(AuthNameGenerator.Randoms.class)) {
            generate.when(() -> AuthNameGenerator.Randoms.getRandomDigits(4)).thenReturn(expectValue);
            helper.issueTokenOrRegisterUser(authUser, new Date(System.nanoTime()));
        }
        // then
        String savedName = String.format("%s%s%s", name, "#", expectValue);
        User findUser = userRepository.findByName(savedName).orElseThrow();
        assertThat(findUser.getEmail()).isEqualTo(uniqueEmail);
        assertThat(findUser.getName()).isEqualTo(savedName);
    }

    private User createUser(String email, String name) {
        return User.builder()
                .email(email)
                .name(name)
                .profileImg("profile")
                .password("password")
                .role(Role.USER)
                .social(Social.DEFAULT)
                .build();
    }

}