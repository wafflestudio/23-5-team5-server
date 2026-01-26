package com.team5.studygroup.oauth.dto

import io.swagger.v3.oas.annotations.media.Schema

data class SocialLoginRequest(
    // Google idToken
    @Schema(description = "구글에서 받은 ID Token")
    val token: String,
)
