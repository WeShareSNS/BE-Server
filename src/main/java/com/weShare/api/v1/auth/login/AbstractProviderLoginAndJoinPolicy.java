package com.weShare.api.v1.auth.login;

import com.weShare.api.v1.auth.controller.dto.TokenDto;
import com.weShare.api.v1.common.CustomUUID;
import com.weShare.api.v1.domain.user.Role;
import com.weShare.api.v1.domain.user.entity.User;
import com.weShare.api.v1.domain.user.repository.UserRepository;
import com.weShare.api.v1.jwt.JwtService;
import com.weShare.api.v1.token.RefreshToken;
import com.weShare.api.v1.token.RefreshTokenRepository;
import com.weShare.api.v1.token.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;

@Component
@RequiredArgsConstructor
public abstract class AbstractProviderLoginAndJoinPolicy implements AuthLoginPolicy {
    protected final Environment evn;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Transactional
    protected User createAuthUser(String email, String profileImg, LocalDate birthDate) {
        User user = createUserWithBirthDate(email, profileImg, birthDate);
        return userRepository.save(user);
    }

    @Transactional
    protected User createAuthUser(String email, String profileImg) {
        User user = createUserWithoutBirthDate(email, profileImg);
        return userRepository.save(user);
    }

    private User createUserWithBirthDate(String email, String profileImg, LocalDate birthDate) {
        return User.builder()
                .email(email)
                .name(CustomUUID.getCustomUUID(16, ""))
                .profileImg(profileImg)
                .role(Role.USER)
                .password(CustomUUID.getCustomUUID(16, ""))
                .birthDate(birthDate)
                .build();
    }

    private User createUserWithoutBirthDate(String email, String profileImg) {
        return User.builder()
                .email(email)
                .name(CustomUUID.getCustomUUID(16, ""))
                .profileImg(profileImg)
                .role(Role.USER)
                .password(CustomUUID.getCustomUUID(16, ""))
                .build();
    }

    protected void reissueRefreshTokenByUser(User user, String refreshToken) {
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

    protected TokenDto getTokenDto(User user, Date issuedAt) {
        String accessToken = jwtService.generateAccessToken(user, issuedAt);
        String refreshToken = jwtService.generateRefreshToken(user, issuedAt);
        return new TokenDto(accessToken, refreshToken);
    }

}
