package com.weShare.api.v1.auth;

import com.weShare.api.v1.domain.token.Token;
import com.weShare.api.v1.domain.token.TokenType;
import com.weShare.api.v1.domain.user.Role;
import com.weShare.api.v1.domain.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weShare.api.v1.config.jwt.JwtService;
import com.weShare.api.v1.domain.token.TokenRepository;
import com.weShare.api.v1.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository repository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public AuthenticationResponse register(RegisterRequest request) {
    validateEmail(request);
    User savedUser = repository.save(createUser(request));

    String jwtToken = jwtService.generateAccessToken(savedUser);
    String refreshToken = jwtService.generateRefreshToken(savedUser);

    createTokenWithUser(savedUser, jwtToken);
    return createAuthenticationResponse(refreshToken, jwtToken);
  }

  private void validateEmail(RegisterRequest request) {
    repository.findByEmail(request.getEmail())
            .ifPresent(user -> {
              throw new IllegalArgumentException(String.format("%s은 가입된 이메일 입니다.", user.getEmail()));});
  }

  private User createUser(RegisterRequest request) {
    return User.builder()
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .username(request.getUsername())
            .birthDate(request.getBirthDate())
            .role(Role.USER)
            .build();
  }

  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword())
    );

    User user = getUserByEmailOrThrowException(request.getEmail());

    String accessToken = jwtService.generateAccessToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);
    revokeAllUserTokens(user);
    tokenRepository.save(createTokenWithUser(user, accessToken));
    return createAuthenticationResponse(refreshToken, accessToken);
  }

  private User getUserByEmailOrThrowException(String email) {
    return repository.findByEmail(email)
            .orElseThrow(() -> {
              throw new IllegalArgumentException("사용자가 존재하지 않습니다.");
            });
  }
  private void revokeAllUserTokens(User user) {
    List<Token> validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
    if (validUserTokens.isEmpty()) return;

    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });

    //하아 db로 건드니까 이런게 문제네 휘발성 데이터 처리 (deleteAll => select Query + deletequery => batch 돌리는게 맞나... 이게)
    tokenRepository.saveAll(validUserTokens);
  }

  private Token createTokenWithUser(User user, String jwtToken) {
    return Token.builder()
        .user(user)
        .token(jwtToken)
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();
  }


  private AuthenticationResponse createAuthenticationResponse(String refreshToken, String accessToken) {
    return AuthenticationResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
  }

  public void refreshToken(
          HttpServletRequest request,
          HttpServletResponse response
  ) throws IOException {

    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (authHeader == null || !authHeader.startsWith(TokenType.BEARER.getType())) {
      return;
    }
    final String refreshToken = authHeader.substring(7);
    final String userEmail = jwtService.extractEmail(refreshToken);
    if (userEmail != null) {
      User user = getUserByEmailOrThrowException(userEmail);
      if (jwtService.isTokenValid(refreshToken, user)) {
        String accessToken = jwtService.generateAccessToken(user);
        revokeAllUserTokens(user);
        tokenRepository.save(createTokenWithUser(user, accessToken));
        AuthenticationResponse authResponse = createAuthenticationResponse(refreshToken, accessToken);
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
      }
    }
  }
}
