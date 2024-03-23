package com.weshare.api.v1.token;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>, RefreshTokenCustomRepository {
}
