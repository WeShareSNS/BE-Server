package com.weShare.api.v1.config.jwt.access;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


public interface AccessTokenRedisRepository extends CrudRepository<AccessTokenFromRedis,String> {

    Optional<AccessTokenFromRedis> findByEmail(String email);
}
