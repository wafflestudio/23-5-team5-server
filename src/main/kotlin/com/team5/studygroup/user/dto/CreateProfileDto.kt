package com.team5.studygroup.user.dto

data class CreateProfileDto(
    val studentNumber: String,
    val major: String,
    val nickname: String,
    val profileImageUrl: String? = null,
    val bio: String? = null,
)
