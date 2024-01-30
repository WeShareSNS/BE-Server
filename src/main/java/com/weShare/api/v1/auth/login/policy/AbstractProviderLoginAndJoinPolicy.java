package com.weShare.api.v1.auth.login.policy;

import com.weShare.api.v1.auth.controller.dto.LoginRequest;
import com.weShare.api.v1.auth.controller.dto.TokenDto;
import com.weShare.api.v1.auth.login.ResponseAuthToken;
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

    @Override
    public TokenDto login(LoginRequest request, Date issuedAt) {
        ResponseAuthToken token = getToken(request.getCode());
        String responseBody = getResponseBody(token.accessToken());
        User authUser = getAuthUser(responseBody);
        //사용자 회원가입 되어 있으면 로그인처리 (다른 auth에서 같은 이메일을 사용할 수 있음...)
        saveAuthUser(authUser);
        TokenDto tokenDto = getTokenDto(authUser, issuedAt);
        reissueRefreshTokenByUser(authUser, tokenDto.refreshToken());
        return tokenDto;
    }

    abstract protected ResponseAuthToken getToken(String code);
    abstract protected String getResponseBody(String accessToken);

    abstract protected User getAuthUser(String responseBody);

    private User saveAuthUser(User user) {
        return userRepository.save(user);
    }

    protected User createAuthUser(String email, String profileImg, LocalDate birthDate) {
        return User.builder()
                .email(email)
                .name(CustomUUID.getCustomUUID(16, ""))
                .profileImg(profileImg)
                .role(Role.USER)
                .password(CustomUUID.getCustomUUID(16, ""))
                .birthDate(birthDate)
                .build();
    }

    protected User createAuthUser(String email, String profileImg) {
        return User.builder()
                .email(email)
                .name(CustomUUID.getCustomUUID(16, ""))
                .profileImg(profileImg)
                .role(Role.USER)
                .password(CustomUUID.getCustomUUID(16, ""))
                .build();
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
