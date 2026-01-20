package com.team5.studygroup.user.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class SignUpDto(
    val username: String,
    val password: String,
    val major: String,
    @JsonProperty("student_number")
    val studentNumber: String,
    val nickname: String,
)
