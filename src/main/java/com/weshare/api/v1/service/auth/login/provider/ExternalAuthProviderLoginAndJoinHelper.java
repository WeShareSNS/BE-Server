package com.weshare.api.v1.service.auth.login.provider;

import com.weshare.api.v1.controller.auth.dto.TokenDto;
import com.weshare.api.v1.domain.user.Social;
import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.domain.user.exception.EmailDuplicateException;
import com.weshare.api.v1.repository.user.UserRepository;
import com.weshare.api.v1.service.auth.login.NotUniqueNameException;
import com.weshare.api.v1.service.auth.login.RetryFailException;
import com.weshare.api.v1.token.RefreshToken;
import com.weshare.api.v1.token.RefreshTokenRepository;
import com.weshare.api.v1.token.TokenType;
import com.weshare.api.v1.token.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class ExternalAuthProviderLoginAndJoinHelper {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Transactional
    public Optional<TokenDto> issueTokenOrRegisterUser(
            User authUser,
            Date issuedAt
    ) {
        Optional<User> findUser = userRepository.findByEmail(authUser.getEmail());
        if (!findUser.isPresent()) {
            try {
                verifyAndSaveUserByName(authUser);
            } catch (NotUniqueNameException e) {
                SaveRetry.retry(()-> {
                    String uniqueNameRandomized = AuthNameGenerator.generateUniqueNameRandomized(authUser.getName());
                    authUser.updateName(uniqueNameRandomized);
                    return verifyAndSaveUserByName(authUser);
                });
            }
            return Optional.empty();
        }
        User existingUser = findUser.get();
        if (!areSocialProvidersEqual(authUser.getSocial(), existingUser.getSocial())) {
            throw new EmailDuplicateException(authUser.getEmail() + "은 기존 사용자이거나 다른 소셜 로그인으로 가입된 회원입니다.");
        }
        return getTokenDto(issuedAt, existingUser);
    }

    private User verifyAndSaveUserByName(User authUser) {
        userRepository.findByName(authUser.getName())
                .ifPresent(user -> { throw new NotUniqueNameException(); });
        return userRepository.save(authUser);
    }

    private Optional<TokenDto> getTokenDto(Date issuedAt, User existingUser) {
        String accessToken = jwtService.generateAccessToken(existingUser, issuedAt);
        String refreshToken = jwtService.generateRefreshToken(existingUser, issuedAt);
        reissueRefreshTokenByUser(existingUser, refreshToken);
        return Optional.of(new TokenDto(
                accessToken,
                refreshToken
        ));
    }

    private boolean areSocialProvidersEqual(Social newUserSocial, Social existingUserSocial) {
        return newUserSocial == existingUserSocial;
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

    @Slf4j
    private static class SaveRetry {
        private static final int RETRY_COUNT = 3;
        private SaveRetry() {
        }
        private static <T> T retry(Supplier<T> supplier) {
            int retries = 0;
            while (true) {
                try {
                    return supplier.get();
                }
                catch (NotUniqueNameException e) {
                    if (retries < RETRY_COUNT) {
                        throw new RetryFailException("사용자 회원가입 재시도 횟수를 초과했습니다. 재요청을 해주세요.", e);
                    }
                    log.error("재시도 시작 현재 count ={}", retries, e);
                    retries++;
                }
            }
        }
    }
}
