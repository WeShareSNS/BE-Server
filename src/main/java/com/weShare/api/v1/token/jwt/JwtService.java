package com.weShare.api.v1.token.jwt;

import com.weShare.api.v1.domain.user.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Component
public class JwtService {
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Getter
    @Value("${application.security.jwt.expiration}")
    private long accessExpiration;

    @Getter
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    public String extractEmail(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (ExpiredJwtException exception) {
            throw new IllegalStateException("Token Expired");
        } catch (JwtException exception) {
            throw new IllegalStateException("Token Tampered");
        } catch (NullPointerException exception) {
            throw new NullPointerException("Token is null");
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateAccessToken(User user) {
        return buildToken(user, accessExpiration);
    }

    public String generateRefreshToken(User user) {
        return buildToken(user, refreshExpiration);
    }

    //test를 위한 메서드를 작성하는게 맞을까...?
    public String generateExpireTestToken(User user) {
        return Jwts
                .builder()
                .setHeader(createHeader())
                .setClaims(createClaims(user))
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    private String buildToken(User user, long expiration) {
        return Jwts
                .builder()
                .setHeader(createHeader())
                .setClaims(createClaims(user))
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.nanoTime()))
                .setExpiration(new Date(System.nanoTime() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractEmail(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Header
    private Map<String, Object> createHeader() {
        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        header.put("alg", "HS256"); // 해시 256 암호화
        return header;
    }

    // Payload
    private Map<String, Object> createClaims(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("username", user.getUsername());
        claims.put("role", user.getRole());
        return claims;
    }

    public long getExpireTimeFromToken(String token){
        final Claims claims = extractAllClaims(token);
        return claims.getExpiration().getTime();
    }
}