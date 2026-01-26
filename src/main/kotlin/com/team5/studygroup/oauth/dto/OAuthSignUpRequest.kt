package com.team5.studygroup.oauth.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "소셜 로그인 추가 정보 입력")
data class OAuthSignUpRequest(
    @Schema(description = "회원가입용 임시 토큰", example = "eyJhbGci...")
    val registerToken: String,
    @Schema(
        description = "서울대 이메일 (소셜 계정이 snu메일이 아닐 경우에만 입력, snu메일일 경우 null 전송)",
        example = "myid@snu.ac.kr",
        nullable = true,
    )
    val email: String? = null,
    val major: String,
    @JsonProperty("student_number")
    val studentNumber: String,
    val nickname: String,
)
