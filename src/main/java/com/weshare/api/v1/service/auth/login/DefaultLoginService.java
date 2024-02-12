package com.weshare.api.v1.service.auth.login;

import com.weshare.api.v1.controller.auth.dto.TokenDto;
import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.repository.user.UserRepository;
import com.weshare.api.v1.token.RefreshToken;
import com.weshare.api.v1.token.RefreshTokenRepository;
import com.weshare.api.v1.token.TokenType;
import com.weshare.api.v1.token.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DefaultLoginService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Transactional
    public Optional<TokenDto> login(
            String email,
            String password,
            Date issuedAt
    ) {
        if (!checkNonBlankValues(email, password)) {
            throw new IllegalStateException("email 혹은 password 값을 확인해주세요");
        }
        User user = getUserByEmailOrThrowException(email);
        if (!isPasswordMatching(password, user.getPassword())) {
            throw new IllegalArgumentException("사용자 정보가 올바르지 않습니다.");
        }
        String accessToken = jwtService.generateAccessToken(user, issuedAt);
        String refreshToken = jwtService.generateRefreshToken(user, issuedAt);
        reissueRefreshTokenByUser(user, refreshToken);
        return Optional.of(new TokenDto(accessToken, refreshToken));
    }

    private boolean checkNonBlankValues(String email, String password) {
        return StringUtils.hasText(email) && StringUtils.hasText(password);
    }

    private User getUserByEmailOrThrowException(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> {
                    throw new UsernameNotFoundException(email + "의 사용자를 찾을 수 없습니다.");
                });
    }

    private boolean isPasswordMatching(String requestPassword, String existingUserPassword) {
        return passwordEncoder.matches(requestPassword, existingUserPassword);
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
