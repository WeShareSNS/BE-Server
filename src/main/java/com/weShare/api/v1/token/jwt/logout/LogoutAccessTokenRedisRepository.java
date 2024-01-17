package com.weShare.api.v1.token.jwt.logout;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface LogoutAccessTokenRedisRepository extends CrudRepository<LogoutAccessTokenFromRedis,String> {
    // @Indexed 사용한 필드만 가능
    Optional<LogoutAccessTokenFromRedis> findByEmail(String email);
}
