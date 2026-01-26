package com.team5.studygroup.oauth.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "소셜 로그인 결과 응답")
data class OAuthLoginResponse(

    @Schema(
        description = "로그인 분기 처리 타입",
        example = "LOGIN",
        allowableValues = ["LOGIN", "REGISTER"]
    )
    val type: String,

    @Schema(
        description = "JWT 토큰 (type이 LOGIN이면 AccessToken, REGISTER면 RegisterToken)"
    )
    val token: String,
)