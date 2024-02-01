package com.weshare.api.v1.jwt.logout;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@RedisHash("logoutAccessToken")
public class LogoutAccessTokenFromRedis {
    @Id
    private String id;

    @TimeToLive
    private Long expiration; // seconds

    @Builder
    private LogoutAccessTokenFromRedis(String id, Long expiration) {
        this.id = id;
        this.expiration = expiration;
    }
}
