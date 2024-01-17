package com.weShare.api.v1.token.jwt.logout;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash("logoutAccessToken")
public class LogoutAccessTokenFromRedis {
    @Id
    private String id;

    @TimeToLive
    private Long expiration; // seconds

    public static LogoutAccessTokenFromRedis createLogoutAccessToken(String accessToken,
                                                                     Long remainingMilliSeconds){
        return LogoutAccessTokenFromRedis.builder()
                .id(accessToken)
                .expiration(remainingMilliSeconds/1000)
                .build();
    }

    @Builder
    private LogoutAccessTokenFromRedis(String id, Long expiration) {
        this.id = id;
        this.expiration = expiration;
    }
}
