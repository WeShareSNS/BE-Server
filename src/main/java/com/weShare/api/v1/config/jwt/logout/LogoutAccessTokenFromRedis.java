package com.weShare.api.v1.config.jwt.logout;

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

    @Indexed // 필드 값으로 데이터 찾을 수 있게 하는 어노테이션(findByAccessToken)
    private String email;

    @TimeToLive
    private Long expiration; // seconds

    public static LogoutAccessTokenFromRedis createLogoutAccessToken(String accessToken, String email,
                                                                                    Long remainingMilliSeconds){
        return LogoutAccessTokenFromRedis.builder()
                .id(accessToken)
                .email(email)
                .expiration(remainingMilliSeconds/1000)
                .build();
    }

    @Builder
    private LogoutAccessTokenFromRedis(String id, String email, Long expiration) {
        this.id = id;
        this.email = email;
        this.expiration = expiration;
    }
}
