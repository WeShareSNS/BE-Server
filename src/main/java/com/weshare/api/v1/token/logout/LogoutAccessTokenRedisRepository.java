package com.weshare.api.v1.token.logout;

import org.springframework.data.repository.CrudRepository;


public interface LogoutAccessTokenRedisRepository extends CrudRepository<LogoutAccessTokenFromRedis,String> {
}
