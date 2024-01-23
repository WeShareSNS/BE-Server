package com.weShare.api.v1.jwt;

import com.weShare.api.v1.domain.user.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String extractEmail(String token);
    String generateAccessToken(User user);
    String generateRefreshToken(User user);
    boolean isTokenValid(String token, UserDetails userDetails);

    void validateToken(String token, User user);

    long getExpireTimeFromToken(String token);
}
