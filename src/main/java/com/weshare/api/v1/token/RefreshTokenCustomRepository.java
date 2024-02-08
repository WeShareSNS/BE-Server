package com.weshare.api.v1.token;

import com.weshare.api.v1.domain.user.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenCustomRepository {
    Optional<RefreshToken> findTokenByUser(User user);
    Optional<User> findUserByToken(String refreshToken);
    Optional<RefreshToken> findTokenByUserEmail(@Param("userEmail") String userEmail);
}
