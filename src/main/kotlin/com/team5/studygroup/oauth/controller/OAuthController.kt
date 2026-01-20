package com.team5.studygroup.oauth.controller

import com.team5.studygroup.oauth.dto.OAuthLoginResponse
import com.team5.studygroup.oauth.dto.SocialLoginRequest
import com.team5.studygroup.oauth.service.OAuthService
import com.team5.studygroup.user.model.ProviderType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/oauth")
class OAuthController(
    private val oauthService: OAuthService,
) {
    @PostMapping("/login/{provider}")
    fun login(
        @RequestBody request: SocialLoginRequest,
        @PathVariable provider: String,
    ): ResponseEntity<OAuthLoginResponse> {
        val providerType =
            try {
                ProviderType.valueOf(provider.uppercase())
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("지원하지 않는 소셜 타입입니다: $provider")
            }
        val response = oauthService.login(providerType, request.token)
        return ResponseEntity.ok(response)
    }
}
