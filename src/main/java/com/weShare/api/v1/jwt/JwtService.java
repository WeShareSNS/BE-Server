package com.weShare.api.v1.jwt;

import com.weShare.api.v1.domain.user.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

public interface JwtService {
    String extractEmail(String token);
    String generateAccessToken(User user, Date issuedAt);
    String generateRefreshToken(User user, Date issuedAt);
    boolean isTokenValid(String token, UserDetails userDetails);

    void validateToken(String token, User user);

    long getExpireTimeFromToken(String token);
}
