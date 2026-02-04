package com.team5.studygroup.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {
    @Bean
    fun openAPI(): OpenAPI {
        // 1. Security 스키마 정의 (자물쇠 모양 만들기)
        val securityScheme =
            SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .name("Authorization")

        return OpenAPI()
            .components(
                Components()
                    .addSecuritySchemes("Authorization", securityScheme),
            )
            .info(
                Info()
                    .title("StudyGroup API")
                    .description("스터디 그룹 서비스 API 명세서")
                    .version("v1.0.0"),
            )
    }
}
