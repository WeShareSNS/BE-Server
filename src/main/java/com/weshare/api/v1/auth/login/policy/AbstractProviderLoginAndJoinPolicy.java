package com.weshare.api.v1.auth.login.policy;

import com.weshare.api.v1.auth.controller.dto.LoginRequest;
import com.weshare.api.v1.auth.controller.dto.TokenDto;
import com.weshare.api.v1.auth.login.ResponseAuthToken;
import com.weshare.api.v1.common.CustomUUID;
import com.weshare.api.v1.domain.Social;
import com.weshare.api.v1.domain.user.Role;
import com.weshare.api.v1.domain.user.entity.User;
import com.weshare.api.v1.domain.user.exception.EmailDuplicateException;
import com.weshare.api.v1.domain.user.repository.UserRepository;
import com.weshare.api.v1.jwt.JwtService;
import com.weshare.api.v1.token.RefreshToken;
import com.weshare.api.v1.token.RefreshTokenRepository;
import com.weshare.api.v1.token.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public abstract class AbstractProviderLoginAndJoinPolicy implements AuthLoginPolicy {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Override
    public TokenDto login(LoginRequest request, Date issuedAt) {
        ResponseAuthToken token = getToken(request.getCode());
        String responseBody = getResponseBody(token.accessToken());
        User authUser = getAuthUser(responseBody);
        saveCheckAuthUser(authUser);
        TokenDto tokenDto = getTokenDto(authUser, issuedAt);
        reissueRefreshTokenByUser(authUser, tokenDto.refreshToken());
        return tokenDto;
    }

    abstract protected ResponseAuthToken getToken(String code);

    abstract protected String getResponseBody(String accessToken);

    abstract protected User getAuthUser(String responseBody);

    private void saveCheckAuthUser(User authUser) {
        if (!isDuplicateUser(authUser)) {
            userRepository.save(authUser);
        }
    }

    protected User createAuthUser(String email, String profileImg, LocalDate birthDate, Social social) {
        return User.builder()
                .email(email)
                .name(CustomUUID.getCustomUUID(16, ""))
                .profileImg(profileImg)
                .role(Role.USER)
                .social(social)
                .password(CustomUUID.getCustomUUID(16, ""))
                .birthDate(birthDate)
                .build();
    }

    protected User createAuthUser(String email, String profileImg, Social social) {
        return User.builder()
                .email(email)
                .name(CustomUUID.getCustomUUID(16, ""))
                .profileImg(profileImg)
                .role(Role.USER)
                .social(social)
                .password(CustomUUID.getCustomUUID(16, ""))
                .build();
    }

    private boolean isDuplicateUser(User newUser) {
        Optional<User> existingUserOptional = userRepository.findByEmail(newUser.getEmail());

        if (existingUserOptional.isPresent()) {
            User existingUser = existingUserOptional.get();
            if (areSocialProvidersEqual(newUser.getSocial(), existingUser.getSocial())) {
                return true;
            }
            throw new EmailDuplicateException(newUser.getEmail() + "은 기존 사용자이거나 다른 소셜 로그인으로 가입된 회원입니다.");
        }
        return false;
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

    private TokenDto getTokenDto(User user, Date issuedAt) {
        String accessToken = jwtService.generateAccessToken(user, issuedAt);
        String refreshToken = jwtService.generateRefreshToken(user, issuedAt);
        return new TokenDto(accessToken, refreshToken);
    }

}
