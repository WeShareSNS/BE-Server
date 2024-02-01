package com.weshare.api.v1.jwt;

import com.weshare.api.v1.token.exception.TokenTimeOutException;
import com.weshare.api.v1.token.exception.InvalidTokenException;
import com.weshare.api.v1.domain.user.entity.User;
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
public class JwtServiceImpl implements JwtService{

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Getter
    @Value("${application.security.jwt.expiration}")
    private long accessExpiration;

    @Getter
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    @Override
    public String extractEmail(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (ExpiredJwtException exception) {
            throw new TokenTimeOutException("만료된 토큰 입니다.");
        } catch (JwtException | NullPointerException exception ) {
            throw new InvalidTokenException("토큰이 유효하지 않습니다.");
        }
    }

    private  <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    @Override
    public String generateAccessToken(User user, Date issuedAt) {
        return buildToken(user, issuedAt, accessExpiration);
    }

    @Override
    public String generateRefreshToken(User user, Date issuedAt) {
        return buildToken(user, issuedAt, refreshExpiration);
    }

    private String buildToken(User user, Date issuedAt, long expiration) {
        return Jwts
                .builder()
                .setHeader(createHeader())
                .setClaims(createClaims(user))
                .setSubject(user.getEmail())
                .setIssuedAt(issuedAt)
                .setExpiration(new Date(issuedAt.getTime() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            String email = extractEmail(token);
            return (email.equals(userDetails.getUsername())) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    @Override
    public long getExpireTimeFromToken(String token){
        try {
            final Claims claims = extractAllClaims(token);
            return claims.getExpiration().getTime();
        } catch (ExpiredJwtException exception) {
            throw new TokenTimeOutException("만료된 토큰 입니다.");
        } catch (JwtException | NullPointerException exception ) {
            throw new InvalidTokenException("토큰이 유효하지 않습니다.");
        }
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

    private Map<String, Object> createHeader() {
        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        header.put("alg", "HS256"); // 해시 256 암호화
        return header;
    }

    private Map<String, Object> createClaims(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("username", user.getUsername());
        claims.put("role", user.getRole());
        return claims;
    }
}