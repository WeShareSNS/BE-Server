package com.weShare.api.v1.auth;

import com.weShare.api.v1.token.jwt.logout.LogoutAccessTokenFromRedis;
import com.weShare.api.v1.token.jwt.logout.LogoutAccessTokenRedisRepository;
import com.weShare.api.v1.token.RefreshToken;
import com.weShare.api.v1.token.TokenType;
import com.weShare.api.v1.domain.user.Role;
import com.weShare.api.v1.domain.user.entity.User;
import com.weShare.api.v1.token.jwt.JwtService;
import com.weShare.api.v1.token.RefreshTokenRepository;
import com.weShare.api.v1.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {
    //여기 멤버변수가 너무 많아서 테스트도 많아
    private final UserRepository repository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final LogoutAccessTokenRedisRepository logoutTokenRedisRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public User signup(SignupRequest request) {
        validateEmail(request);
        return repository.save(createUser(request));
    }

    private void validateEmail(SignupRequest request) {
        repository.findByEmail(request.getEmail())
                .ifPresent(user -> {
                    throw new IllegalArgumentException(String.format("%s은 가입된 이메일 입니다.", user.getEmail()));
                });
    }

    private User createUser(SignupRequest request) {
        return User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(getDefaultUsername())
                .birthDate(request.getBirthDate())
                .profileImg(getDefaultProfileImgURL())
                .role(Role.USER)
                .build();
    }

    //우선 16자리로 (중복 올라가도 사용자는 닉네임 변경할꺼같으니까)
    private String getDefaultUsername() {
        return UUID.randomUUID().toString()
                .replaceAll("-", "")
                .substring(0, 16);
    }

    //하드코딩 지우고 좀 고민해보기 s3에 담아서 사용할건지 db에서 사용할건지 yml로 처리할건지
    private String getDefaultProfileImgURL() {
        return "https://static.vecteezy.com/system/resources/thumbnails/020/765/399/small/default-profile-account-unknown-icon-black-silhouette-free-vector.jpg";
    }

    public TokenDto login(LoginRequest request) {
        // 처리해야될지 getUserByEmailOrThrowException 이 메서드에서 예외처리 하면 되지 않을까..?
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword())
        );
        log.info("auth{}",SecurityContextHolder.getContext().getAuthentication());
        User user = getUserByEmailOrThrowException(request.getEmail());

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        reissueRefreshTokenByUser(user, refreshToken);
        return createAuthenticationResponse(refreshToken, accessToken);
    }

    private User getUserByEmailOrThrowException(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> {
                    throw new IllegalArgumentException("사용자가 존재하지 않습니다.");
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

    private TokenDto createAuthenticationResponse(String refreshToken, String accessToken) {
        return new TokenDto(accessToken, refreshToken);
    }

    public TokenDto reissueToken(String refreshToken) {
        User user = findUserByValidRefreshToken(refreshToken);

        String accessToken = jwtService.generateAccessToken(user);
        String reissueToken = jwtService.generateRefreshToken(user);
        reissueRefreshTokenByUser(user, reissueToken);
        return createAuthenticationResponse(reissueToken, accessToken);
    }

    private User findUserByValidRefreshToken(String refreshToken) {
        User user = refreshTokenRepository.findUserByToken(refreshToken)
                .orElseThrow(() -> {
                    throw new IllegalArgumentException("토큰이 유효하지 않습니다.");
                });

        //사용자 토큰이 아닐 때 유효하지 않은 토큰은(위에서 걸러지고)
        // expire는 tokenValid에서 예외 던짐 (catch에서 false 반환하는 방법도 있음)
        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new IllegalArgumentException("토큰이 유효하지 않습니다.");
        }

        return user;
    }

    public void logout(final String jwt) {
        // refresh token 삭제하기
        if (logoutTokenRedisRepository.existsById(jwt)) {
            // 4xx대 예외를 던지는게 맞을까? 304처럼...
            // return;
            throw new IllegalArgumentException("이미 로그아웃된 사용자 입니다.");
        }

        String userEmail = jwtService.extractEmail(jwt);
        saveLogoutToken(jwt);
        //refresh token 지워야함
        refreshTokenRepository.findTokenByUserEmail(userEmail)
                        .ifPresent(this::deleteRefreshToken);
        // context 지워주기 (먹히는지 모르겠음)
        SecurityContextHolder.clearContext();
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
