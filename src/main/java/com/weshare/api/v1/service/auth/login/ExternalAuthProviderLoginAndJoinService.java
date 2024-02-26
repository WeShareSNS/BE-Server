package com.weshare.api.v1.service.auth.login;

import com.weshare.api.v1.controller.auth.dto.TokenDto;
import com.weshare.api.v1.domain.user.Social;
import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.domain.user.exception.EmailDuplicateException;
import com.weshare.api.v1.repository.user.UserRepository;
import com.weshare.api.v1.service.auth.login.provider.AuthProvider;
import com.weshare.api.v1.token.RefreshToken;
import com.weshare.api.v1.token.RefreshTokenRepository;
import com.weshare.api.v1.token.TokenType;
import com.weshare.api.v1.token.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExternalAuthProviderLoginAndJoinService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final AuthProvider authProvider;

    public Optional<TokenDto> login(
            String providerName,
            String code,
            Date issuedAt
    ) {
        //외부 api는 transaction 내부에서 처리하지 말기
        if (!checkNonBlankValues(providerName, code)) {
            throw new IllegalStateException("providerName 혹은 code 값을 확인해주세요");
        }
        User authUser = authProvider.getAuthUserByExternalProvider(providerName, code);
        return issueTokenOrRegisterUser(authUser, issuedAt);
    }

    private boolean checkNonBlankValues(String providerName, String code) {
        return StringUtils.hasText(providerName) && StringUtils.hasText(code);
    }

    @Transactional
    protected Optional<TokenDto> issueTokenOrRegisterUser(
            User authUser,
            Date issuedAt
    ) {
        Optional<User> findUser = userRepository.findByEmail(authUser.getEmail());
        if (!findUser.isPresent()) {
            userRepository.save(authUser);
            return Optional.empty();
        }

        User existingUser = findUser.get();
        if (!areSocialProvidersEqual(authUser.getSocial(), existingUser.getSocial())) {
            throw new EmailDuplicateException(authUser.getEmail() + "은 기존 사용자이거나 다른 소셜 로그인으로 가입된 회원입니다.");
        }
        String accessToken = jwtService.generateAccessToken(existingUser, issuedAt);
        String refreshToken = jwtService.generateRefreshToken(existingUser, issuedAt);
        reissueRefreshTokenByUser(existingUser, refreshToken);
        return Optional.of(new TokenDto(
                accessToken,
                refreshToken
        ));
    }

    private boolean areSocialProvidersEqual(Social newUserSocial, Social existingUserSocial) {
        // 소셜 정보가 같으면 true, 다르면 false 반환
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

}
