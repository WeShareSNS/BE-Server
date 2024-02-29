package com.weshare.api.v1.filter;

import com.weshare.api.v1.token.TokenType;
import com.weshare.api.v1.token.exception.InvalidTokenException;
import com.weshare.api.v1.token.exception.TokenTimeOutException;
import com.weshare.api.v1.token.jwt.JwtService;
import com.weshare.api.v1.token.logout.LogoutAccessTokenRedisRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService customUserDetailsService;
    private final LogoutAccessTokenRedisRepository logoutTokenRedisRepository;
    private final FilterExceptionHandler filterExceptionHandler;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        if (request.getServletPath().contains("/api/v1/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(TokenType.BEARER.getType())) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        if (logoutTokenRedisRepository.existsById(jwt)) {
            //로그아웃 된 토큰이라고 예외던지기
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String userEmail = jwtService.extractEmail(jwt);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEmail);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (TokenTimeOutException e) {
            filterExceptionHandler.handleAuthenticationExceptionMessage(request,response, HttpStatus.UNAUTHORIZED, FilterErrorCode.TOKEN_TIME_OUT_ERROR);
        } catch (InvalidTokenException e) {
            filterExceptionHandler.handleAuthenticationExceptionMessage(request,response,HttpStatus.UNAUTHORIZED, FilterErrorCode.INVALID_TOKEN_ERROR);
        } catch (UsernameNotFoundException e) {
            filterExceptionHandler.handleAuthenticationExceptionMessage(request,response,HttpStatus.NOT_FOUND, FilterErrorCode.USER_NOT_FOUND_ERROR);
        }

    }

}
