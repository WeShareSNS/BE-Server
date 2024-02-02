package com.weshare.api.v1.token.jwt;

import com.weshare.api.v1.domain.user.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

public interface JwtService {
    String extractEmail(String token);
    String generateAccessToken(User user, Date issuedAt);
    String generateRefreshToken(User user, Date issuedAt);
    boolean isTokenValid(String token, UserDetails userDetails);
    long getExpireTimeFromToken(String token);
}
