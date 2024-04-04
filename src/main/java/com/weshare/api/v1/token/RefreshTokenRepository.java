package com.weshare.api.v1.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

// user로 조회하는거 지우던지 하기
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>, RefreshTokenCustomRepository {
    @Query("""
            select t from RefreshToken t 
            join fetch t.user 
            where t.user.id = :userId
            """)
    Optional<RefreshToken> findByUserId(Long userId);
}
