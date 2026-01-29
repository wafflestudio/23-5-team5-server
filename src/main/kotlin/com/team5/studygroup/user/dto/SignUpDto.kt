package com.team5.studygroup.user.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class SignUpDto(
    @field:Schema(
        description = "아이디(스누메일)",
        example = "waffle@snu.ac.kr",
    )
    @field:Pattern(
        regexp = "^.+@snu\\.ac\\.kr$",
        message = "아이디는 @snu.ac.kr로 끝나야 합니다",
    )
    val username: String,
    @field:Schema(example = "password1234")
    @field:Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다")
    val password: String,
    @field:Schema(example = "컴퓨터공학부")
    @field:NotBlank(message = "전공은 필수입니다")
    val major: String,
    @field:Schema(example = "2024-12345")
    @field:NotBlank(message = "학번은 필수입니다")
    @JsonProperty("student_number")
    val studentNumber: String,
    @field:Schema(example = "스누피")
    @field:NotBlank(message = "닉네임은 필수입니다")
    val nickname: String,
)
