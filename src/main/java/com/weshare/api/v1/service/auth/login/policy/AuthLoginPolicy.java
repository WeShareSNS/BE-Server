package com.weshare.api.v1.service.auth.login.policy;

import com.weshare.api.v1.controller.auth.dto.LoginRequest;
import com.weshare.api.v1.controller.auth.dto.TokenDto;

import java.util.Date;

public interface AuthLoginPolicy {
    TokenDto login(LoginRequest request, Date issuedAt);
    boolean isIdentityProvider(String providerName);
}
