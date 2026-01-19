package com.team5.studygroup.user.dto

data class LoginResponseDto(
    val accessToken: String,
    val nickname: String,
    val isVerified: Boolean,
)
