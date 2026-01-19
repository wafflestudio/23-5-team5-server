package com.team5.studygroup.user.dto

data class SignUpResponseDto(
    val accessToken: String,
    val username: String,
    val nickname: String,
    val isVerified: Boolean,
)
