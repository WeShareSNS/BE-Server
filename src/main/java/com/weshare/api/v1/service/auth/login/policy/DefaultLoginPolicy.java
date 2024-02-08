package com.weshare.api.v1.service.auth.login.policy;

import com.weshare.api.v1.controller.auth.dto.LoginRequest;
import com.weshare.api.v1.controller.auth.dto.TokenDto;
import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.repository.user.UserRepository;
import com.weshare.api.v1.token.jwt.JwtService;
import com.weshare.api.v1.token.RefreshToken;
import com.weshare.api.v1.token.RefreshTokenRepository;
import com.weshare.api.v1.token.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static com.weshare.api.v1.domain.user.Social.DEFAULT;

@Component
@RequiredArgsConstructor
public class DefaultLoginPolicy implements AuthLoginPolicy {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Transactional
    @Override
    public TokenDto login(LoginRequest request, Date issuedAt) {
        User user = getUserByEmailOrThrowException(request.getEmail());
        if (!isPasswordMatching(request, user)) {
            throw new IllegalArgumentException("사용자 정보가 올바르지 않습니다.");
        }
        String accessToken = jwtService.generateAccessToken(user, issuedAt);
        String refreshToken = jwtService.generateRefreshToken(user, issuedAt);
        reissueRefreshTokenByUser(user, refreshToken);
        return new TokenDto(accessToken, refreshToken);
    }

    private User getUserByEmailOrThrowException(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> {
                    throw new UsernameNotFoundException(email + "의 사용자를 찾을 수 없습니다.");
                });
    }

    private boolean isPasswordMatching(LoginRequest request, User user) {
        return passwordEncoder.matches(request.getPassword(), user.getPassword());
    }

    @Override
    public boolean isIdentityProvider(String providerName) {
        return DEFAULT.getProviderName().equals(providerName);
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

}
