package com.team5.studygroup.verification.dto

import io.swagger.v3.oas.annotations.media.Schema

data class VerifyRequest(
    @Schema(description = "인증번호 발송된 snu메일")
    val email: String,
    @Schema(description = "인증번호")
    val code: String,
)
