package com.weshare.api.v1.config.social;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.security.oauth2.client")
public class KakaoLoginProperties {

    private final Provider provider;
    private final Registration registration;

    public String getTokenUri() {
        return provider.getTokenUri();
    }

    public String getUserInfoUri() {
        return provider.getUserInfoUri();
    }

    public String getAuthorizationGrantType() {
        return registration.getAuthorizationGrantType();
    }

    public String getClientId() {
        return registration.getClientId();
    }

    public String getClientSecret() {
        return registration.getClientSecret();
    }

    public String getRedirectUri() {
        return registration.getRedirectUri();
    }

    public KakaoLoginProperties(Provider provider, Registration registration) {
        this.provider = provider;
        this.registration = registration;
    }

    private static class Provider {

        private final Kakao kakao;

        public Provider(Kakao kakao) {
            this.kakao = kakao;
        }

        private String getTokenUri() {
            return kakao.tokenUri;
        }

        private String getUserInfoUri() {
            return kakao.userInfoUri;
        }

        private static class Kakao {
            private final String tokenUri;
            private final String userInfoUri;

            public Kakao(String tokenUri, String userInfoUri) {
                this.tokenUri = tokenUri;
                this.userInfoUri = userInfoUri;
            }
        }

    }

    private static class Registration {
        @Getter
        private final Kakao kakao;

        public Registration(Kakao kakao) {
            this.kakao = kakao;
        }

        private String getAuthorizationGrantType() {
            return kakao.authorizationGrantType;
        }

        private String getClientId() {
            return kakao.clientId;
        }

        private String getClientSecret() {
            return kakao.clientSecret;
        }

        private String getRedirectUri() {
            return kakao.redirectUri;
        }

        @Getter
        private static class Kakao {
            private final String authorizationGrantType;
            private final String clientId;
            private final String clientSecret;
            private final String redirectUri;

            public Kakao(String authorizationGrantType, String clientId, String clientSecret, String redirectUri) {
                this.authorizationGrantType = authorizationGrantType;
                this.clientId = clientId;
                this.clientSecret = clientSecret;
                this.redirectUri = redirectUri;
            }
        }

    }
}
