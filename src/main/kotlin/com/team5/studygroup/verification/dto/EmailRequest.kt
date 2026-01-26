package com.team5.studygroup.verification.dto

import io.swagger.v3.oas.annotations.media.Schema

data class EmailRequest(
    @Schema(description = "인증번호 보낼 snu메일 주소")
    val email: String,
)
