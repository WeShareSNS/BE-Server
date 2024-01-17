package com.weShare.api.v1.config.jwt.access;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;


@Getter
@RedisHash("refreshToken")
public class AccessTokenFromRedis {
    @Id
    private String id;

    @Indexed
    private String email;

    @TimeToLive
    private Long expiration;

    @Builder
    private AccessTokenFromRedis(String id, String email, Long expiration) {
        this.id = id;
        this.email = email;
        this.expiration = expiration;
    }

    public static AccessTokenFromRedis createRefreshToken(String refreshToken, String email,
                                                          Long remainingMilliSeconds){
        return AccessTokenFromRedis.builder()
                .id(refreshToken)
                .email(email)
                .expiration(remainingMilliSeconds/1000)
                .build();
    }
}
