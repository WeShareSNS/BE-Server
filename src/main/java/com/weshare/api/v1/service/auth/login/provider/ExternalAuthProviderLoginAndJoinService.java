package com.weshare.api.v1.service.auth.login.provider;

import com.weshare.api.v1.controller.auth.dto.TokenDto;
import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.service.auth.login.provider.AuthProvider;
import com.weshare.api.v1.service.auth.login.provider.ExternalAuthProviderLoginAndJoinHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExternalAuthProviderLoginAndJoinService {
    private final AuthProvider authProvider;
    private final ExternalAuthProviderLoginAndJoinHelper helper;

    public Optional<TokenDto> login(
            String providerName,
            String code,
            Date issuedAt
    ) {
        // 외부 api는 transaction 내부에서 처리하지 말기
        if (!checkNonBlankValues(providerName, code)) {
            throw new IllegalStateException("providerName 혹은 code 값을 확인해주세요");
        }
        // 컨트롤러에서 외부서비스 호출해야 하는데 구조상 문제로 트랜잭션 없이 호출
        User authUser = authProvider.getAuthUserByExternalProvider(providerName, code);
        // 새로운 트랜잭션 생성해서 처리
        return helper.issueTokenOrRegisterUser(authUser, issuedAt);
    }

    private boolean checkNonBlankValues(String providerName, String code) {
        return StringUtils.hasText(providerName) && StringUtils.hasText(code);
    }
}
