package com.weShare.api.v1.auth.login;

import com.weShare.api.v1.auth.controller.dto.LoginRequest;
import com.weShare.api.v1.auth.controller.dto.TokenDto;
import com.weShare.api.v1.domain.user.entity.User;
import com.weShare.api.v1.domain.user.repository.UserRepository;
import com.weShare.api.v1.jwt.JwtService;
import com.weShare.api.v1.token.RefreshToken;
import com.weShare.api.v1.token.RefreshTokenRepository;
import com.weShare.api.v1.token.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class DefaultLoginPolicy implements AuthLoginPolicy {

    private static final String PROVIDER_NAME = "default";
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Transactional
    @Override
    public TokenDto login(LoginRequest request, Date issuedAt) {
        User user = getUserByEmailOrThrowException(request.getEmail());
        validatePassword(request, user);
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

    private void validatePassword(LoginRequest request, User user) {
        Assert.isTrue(
                passwordEncoder.matches(request.getPassword(), user.getPassword()),
                "사용자 정보가 올바르지 않습니다."
        );
    }

    @Override
    public boolean isIdentityProvider(String providerName) {
        return PROVIDER_NAME.equals(providerName);
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
