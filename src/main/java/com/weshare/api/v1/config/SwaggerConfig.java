package com.weshare.api.v1.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(title = "We Share API 명세서",
                description = "Spring Boot를 이용한 We Share 웹 어플리케이션 API입니다.",
                version = "v1,0,0")
)
@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi groupedOpenApi() {
        String[] paths = {
                "/api/v1/auth/**",
                "/api/v1/trip/schedules/**",
                "/api/v1/me/**",
        };

        return GroupedOpenApi.builder()
                .group("일반 사용자의 도메인에 대한 API")
                .pathsToMatch(paths)
                .build();
    }
}
