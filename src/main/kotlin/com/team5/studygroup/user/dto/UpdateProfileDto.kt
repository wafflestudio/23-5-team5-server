package com.team5.studygroup.user.dto

data class UpdateProfileDto(
    val major: String,
    val nickname: String,
    val profileImageUrl: String,
    val bio: String,
)
