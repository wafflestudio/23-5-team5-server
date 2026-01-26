package com.team5.studygroup.verification.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

data class SocialVerifyRequest(
    @Schema(description = "회원가입용 임시 토큰")
    @JsonProperty("register_token")
    val registerToken: String,
    @Schema(description = "인증번호 발송된 snu메일")
    val email: String,
    @Schema(description = "인증번호")
    val code: String,
)
