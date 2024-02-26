package com.weshare.api.v1.config.social;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.security.oauth2.client")
public class NaverLoginProperties {

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

    public String getState() {
        return registration.getState();
    }

    public NaverLoginProperties(Provider provider, Registration registration) {
        this.provider = provider;
        this.registration = registration;
    }

    private static class Provider {

        private final Naver naver;

        public Provider(Naver naver) {
            this.naver = naver;
        }

        private String getTokenUri() {
            return naver.tokenUri;
        }

        private String getUserInfoUri() {
            return naver.userInfoUri;
        }

        private static class Naver {
            private final String tokenUri;
            private final String userInfoUri;

            public Naver(String tokenUri, String userInfoUri) {
                this.tokenUri = tokenUri;
                this.userInfoUri = userInfoUri;
            }
        }

    }

    private static class Registration {
        private final Naver naver;

        public Registration(Naver naver) {
            this.naver = naver;
        }

        private String getAuthorizationGrantType() {
            return naver.authorizationGrantType;
        }

        private String getClientId() {
            return naver.clientId;
        }

        private String getClientSecret() {
            return naver.clientSecret;
        }

        private String getRedirectUri() {
            return naver.redirectUri;
        }

        private String getState() {
            return naver.state;
        }

        private static class Naver {
            private final String authorizationGrantType;
            private final String clientId;
            private final String clientSecret;
            private final String redirectUri;
            private final String state;

            public Naver(String authorizationGrantType, String clientId, String clientSecret, String redirectUri, String state) {
                this.authorizationGrantType = authorizationGrantType;
                this.clientId = clientId;
                this.clientSecret = clientSecret;
                this.redirectUri = redirectUri;
                this.state = state;
            }
        }

    }
}
