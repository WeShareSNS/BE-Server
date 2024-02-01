package com.weshare.api.v1.token.jwt.logout;

import com.weshare.api.IntegrationTestSupport;
import com.weshare.api.v1.jwt.logout.LogoutAccessTokenFromRedis;
import com.weshare.api.v1.jwt.logout.LogoutAccessTokenRedisRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class LogoutAccessTokenRedisRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private LogoutAccessTokenRedisRepository logoutAccessTokenRedisRepository;

    @AfterEach
    void tearDown(){
        logoutAccessTokenRedisRepository.deleteAll();
    }

    @Test
    @DisplayName("accessToken가 존재하는지 확인할 수 있다.")
    public void existsById() {
        // given
        String token = "accessToken";
        createAndSaveLogoutToken(token);
        // when
        boolean isContains = logoutAccessTokenRedisRepository.existsById(token);
        // then
        Assertions.assertTrue(isContains);
    }

    private LogoutAccessTokenFromRedis createAndSaveLogoutToken(String token) {
        LogoutAccessTokenFromRedis logoutToken = LogoutAccessTokenFromRedis.builder()
                .id(token)
                .expiration(1000L)
                .build();

        LogoutAccessTokenFromRedis save = logoutAccessTokenRedisRepository.save(logoutToken);
        return save;
    }

}