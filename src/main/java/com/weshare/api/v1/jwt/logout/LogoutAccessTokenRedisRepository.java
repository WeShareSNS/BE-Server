package com.weshare.api.v1.jwt.logout;

import org.springframework.data.repository.CrudRepository;


public interface LogoutAccessTokenRedisRepository extends CrudRepository<LogoutAccessTokenFromRedis,String> {
}
