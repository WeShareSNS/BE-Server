package com.weShare.api.v1.auth;

import com.weShare.api.v1.auth.exception.EmailDuplicateException;
import com.weShare.api.v1.auth.exception.InvalidTokenException;
import com.weShare.api.v1.auth.exception.TokenNotFoundException;
import com.weShare.api.v1.token.jwt.logout.LogoutAccessTokenFromRedis;
import com.weShare.api.v1.token.jwt.logout.LogoutAccessTokenRedisRepository;
import com.weShare.api.v1.token.RefreshToken;
import com.weShare.api.v1.token.TokenType;
import com.weShare.api.v1.domain.user.Role;
import com.weShare.api.v1.domain.user.entity.User;
import com.weShare.api.v1.token.jwt.JwtService;
import com.weShare.api.v1.token.RefreshTokenRepository;
import com.weShare.api.v1.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final LogoutAccessTokenRedisRepository logoutTokenRedisRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public User signup(SignupRequest request) {
        validateEmail(request);
        return repository.save(createUser(request));
    }

    private void validateEmail(SignupRequest request) {
        repository.findByEmail(request.getEmail())
                .ifPresent(user -> {
                    throw new EmailDuplicateException(user.getEmail() + "은 가입된 이메일 입니다.");
                });
    }

    private User createUser(SignupRequest request) {
        LocalDate birthDate = LocalDate.parse(request.getBirthDate());
        validateDate(birthDate);

        return User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(getDefaultUsername())
                .birthDate(birthDate)
                .profileImg(getDefaultProfileImgURL())
                .role(Role.USER)
                .build();
    }
    private void validateDate(LocalDate birthDate) {
        if (LocalDate.now().isBefore(birthDate)) {
            throw new IllegalArgumentException("생년월일은 미래 날짜를 입력하실 수 없습니다.");
        }
    }

    //우선 16자리로 (중복 올라가도 사용자는 닉네임 변경할꺼같으니까)
    private String getDefaultUsername() {
        return UUID.randomUUID().toString()
                .replaceAll("-", "")
                .substring(0, 16);
    }

    //하드코딩 지우고 좀 고민해보기 s3에 담아서 사용할건지 db에서 사용할건지 yml로 처리할건지 프론트쪽에서 그냥 데이터 넘겨받아야될지도
    private String getDefaultProfileImgURL() {
        return "https://static.vecteezy.com/system/resources/thumbnails/020/765/399/small/default-profile-account-unknown-icon-black-silhouette-free-vector.jpg";
    }

    public TokenDto login(LoginRequest request) {
        User user = getUserByEmailOrThrowException(request.getEmail());

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        reissueRefreshTokenByUser(user, refreshToken);
        return new TokenDto(accessToken, refreshToken);
    }

    private User getUserByEmailOrThrowException(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> {
                    throw new UsernameNotFoundException(email + "의 사용자를 찾을 수 없습니다.");
                });
    }

    private void reissueRefreshTokenByUser(User user, String refreshToken) {
        RefreshToken token = refreshTokenRepository.findTokenByUser(user)
                .orElse(createRefreshTokenWithUser(user, refreshToken));

        token.updateToken(refreshToken);
        refreshTokenRepository.save(token);
    }

    private RefreshToken createRefreshTokenWithUser(User user, String refreshToken) {
        return RefreshToken.builder()
                .user(user)
                .token(refreshToken)
                .tokenType(TokenType.BEARER)
                .build();
    }

    public TokenDto reissueToken(Optional<String> token) {
        String refreshToken = validateToken(token);
        User user = findUserByValidRefreshToken(refreshToken);

        String accessToken = jwtService.generateAccessToken(user);
        String reissueToken = jwtService.generateRefreshToken(user);
        reissueRefreshTokenByUser(user, reissueToken);
        return new TokenDto(accessToken, reissueToken);
    }

    private static String validateToken(Optional<String> token) {
        return token.orElseThrow(() -> {
            throw new InvalidTokenException("토큰이 존재하지 않습니다.");
        });
    }

    private User findUserByValidRefreshToken(String refreshToken) {
        User user = refreshTokenRepository.findUserByToken(refreshToken)
                .orElseThrow(() -> {
                    throw new TokenNotFoundException("Refresh Token이 존재하지 않습니다.");
                });

        jwtService.validateToken(refreshToken, user);
        return user;
    }

    public void logout(final String jwt) {
        String userEmail = jwtService.extractEmail(jwt);
        saveLogoutToken(jwt);
        refreshTokenRepository.findTokenByUserEmail(userEmail)
                        .ifPresent(this::deleteRefreshToken);
    }

    private void saveLogoutToken(String accessToken) {
        long expireTimeFromToken = jwtService.getExpireTimeFromToken(accessToken);

        LogoutAccessTokenFromRedis logoutToken = LogoutAccessTokenFromRedis.builder()
                .id(accessToken)
                .expiration(expireTimeFromToken)
                .build();

        logoutTokenRedisRepository.save(logoutToken);
    }

    private void deleteRefreshToken(RefreshToken refreshToken) {
        refreshTokenRepository.delete(refreshToken);
    }
}
