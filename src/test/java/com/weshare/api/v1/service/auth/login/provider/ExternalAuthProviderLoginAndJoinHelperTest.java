package com.weshare.api.v1.service.auth.login.provider;

import com.weshare.api.v1.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ExternalAuthProviderLoginAndJoinHelperTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    @Rollback
    public void 사용자_이메일_중복시_재시도가_수행된다() {
        // given

        // when

        // then
    }

}