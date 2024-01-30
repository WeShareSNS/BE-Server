package com.weShare.api.v1.auth.login;

import com.weShare.api.v1.auth.controller.dto.LoginRequest;
import com.weShare.api.v1.auth.controller.dto.TokenDto;

import java.util.Date;

public interface AuthLoginPolicy {
    TokenDto login(LoginRequest request, Date issuedAt);
    boolean isIdentityProvider(String providerName);
}
