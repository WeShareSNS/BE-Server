package com.weshare.api.v1.config.social;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.security.oauth2.client")
public class GoogleLoginProperties {

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

    public GoogleLoginProperties(Provider provider, Registration registration) {
        this.provider = provider;
        this.registration = registration;
    }

    private static class Provider {

        private final Google google;

        public Provider(Google google) {
            this.google = google;
        }

        private String getTokenUri() {
            return google.tokenUri;
        }

        private String getUserInfoUri() {
            return google.userInfoUri;
        }

        private static class Google {
            private final String tokenUri;
            private final String userInfoUri;

            public Google(String tokenUri, String userInfoUri) {
                this.tokenUri = tokenUri;
                this.userInfoUri = userInfoUri;
            }
        }

    }

    private static class Registration {
        @Getter
        private final Google google;

        public Registration(Google google) {
            this.google = google;
        }

        private String getAuthorizationGrantType() {
            return google.authorizationGrantType;
        }

        private String getClientId() {
            return google.clientId;
        }

        private String getClientSecret() {
            return google.clientSecret;
        }

        private String getRedirectUri() {
            return google.redirectUri;
        }

        @Getter
        private static class Google {
            private final String authorizationGrantType;
            private final String clientId;
            private final String clientSecret;
            private final String redirectUri;

            public Google(String authorizationGrantType, String clientId, String clientSecret, String redirectUri) {
                this.authorizationGrantType = authorizationGrantType;
                this.clientId = clientId;
                this.clientSecret = clientSecret;
                this.redirectUri = redirectUri;
            }
        }

    }
}
