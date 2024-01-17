package com.weShare.api.v1.token.jwt.logout;

import org.springframework.data.repository.CrudRepository;

// @Indexed 사용한 필드만 가능
public interface LogoutAccessTokenRedisRepository extends CrudRepository<LogoutAccessTokenFromRedis,String> {
}
