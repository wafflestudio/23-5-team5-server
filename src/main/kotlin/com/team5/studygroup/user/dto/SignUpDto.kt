package com.team5.studygroup.user.dto

data class SignUpDto(
    val username: String,
    val password: String,
    val email: String,
    val major: String,
    val studentNumber: String,
    val nickname: String,
)
