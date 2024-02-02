package com.weshare.api.v1.service.auth;

import com.weshare.api.v1.controller.auth.dto.DuplicateEmailRequest;
import com.weshare.api.v1.controller.auth.dto.LoginRequest;
import com.weshare.api.v1.controller.auth.dto.SignupRequest;
import com.weshare.api.v1.controller.auth.dto.TokenDto;
import com.weshare.api.v1.service.auth.login.AuthLoginService;
import com.weshare.api.v1.common.CustomUUID;
import com.weshare.api.v1.domain.user.Social;
import com.weshare.api.v1.domain.user.exception.EmailDuplicateException;
import com.weshare.api.v1.token.exception.InvalidTokenException;
import com.weshare.api.v1.token.exception.TokenNotFoundException;
import com.weshare.api.v1.token.logout.LogoutAccessTokenFromRedis;
import com.weshare.api.v1.token.logout.LogoutAccessTokenRedisRepository;
import com.weshare.api.v1.token.RefreshToken;
import com.weshare.api.v1.token.TokenType;
import com.weshare.api.v1.domain.user.Role;
import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.token.jwt.JwtService;
import com.weshare.api.v1.token.RefreshTokenRepository;
import com.weshare.api.v1.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {
    private static final String DEFAULT_PROFILE_IMG_URL = "https://static.vecteezy.com/system/resources/thumbnails/020/765/399/small/default-profile-account-unknown-icon-black-silhouette-free-vector.jpg";

    private final UserRepository repository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final LogoutAccessTokenRedisRepository logoutTokenRedisRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthLoginService loginService;

    public User join(SignupRequest request) {
        String email = request.getEmail();
        if (isDuplicateEmail(email)) {
            throw new EmailDuplicateException(email + "은 가입된 이메일 입니다.");
        }
        return repository.save(createUser(request));
    }

    private boolean isDuplicateEmail(String email) throws EmailDuplicateException {
        return repository.findByEmail(email).isPresent();
    }

    public void checkDuplicateEmailForSignup(DuplicateEmailRequest request) {
        String email = request.getEmail();
        if (isDuplicateEmail(email)) {
            throw new EmailDuplicateException(email + "은 가입된 이메일 입니다.");
        }
    }

    public TokenDto login(LoginRequest request, Date issuedAt) {
        return loginService.login(request, issuedAt);
    }

    private User createUser(SignupRequest request) {
        LocalDate birthDate = LocalDate.parse(request.getBirthDate());
        if (isBirthDateInFuture(birthDate)) {
            throw new IllegalArgumentException("생년월일은 미래 날짜를 입력하실 수 없습니다.");
        }

        return User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(CustomUUID.getCustomUUID(16, ""))
                .birthDate(birthDate)
                .profileImg(DEFAULT_PROFILE_IMG_URL)
                .role(Role.USER)
                .social(Social.DEFAULT)
                .build();
    }

    private boolean isBirthDateInFuture(LocalDate birthDate) {
        return LocalDate.now().isBefore(birthDate);
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

    public TokenDto reissueToken(Optional<String> token, Date issuedAt) {
        if (token.isEmpty()) {
            throw new InvalidTokenException("토큰이 존재하지 않습니다.");
        }
        String refreshToken = token.get();
        User user = findUserByValidRefreshToken(refreshToken);

        String accessToken = jwtService.generateAccessToken(user, issuedAt);
        String reissueToken = jwtService.generateRefreshToken(user, issuedAt);
        reissueRefreshTokenByUser(user, reissueToken);
        return new TokenDto(accessToken, reissueToken);
    }

    private User findUserByValidRefreshToken(String refreshToken) {
        User user = refreshTokenRepository.findUserByToken(refreshToken)
                .orElseThrow(() -> {
                    throw new TokenNotFoundException("Refresh Token이 존재하지 않습니다.");
                });

        if (jwtService.isTokenValid(refreshToken, user)) {
            throw new InvalidTokenException("토큰이 유효하지 않습니다.");
        }
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
