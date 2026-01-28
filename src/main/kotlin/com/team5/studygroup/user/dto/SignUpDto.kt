package com.team5.studygroup.user.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class SignUpDto(
    @field:Size(min = 4, message = "아이디는 4자 이상이어야 합니다")
    val username: String,

    @field:Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다")
    val password: String,

    @field:NotBlank(message = "전공은 필수입니다")
    val major: String,

    @field:NotBlank(message = "학번은 필수입니다")
    @JsonProperty("student_number")
    val studentNumber: String,

    @field:NotBlank(message = "닉네임은 필수입니다")
    val nickname: String,
)
