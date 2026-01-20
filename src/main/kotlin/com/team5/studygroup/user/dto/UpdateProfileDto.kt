package com.team5.studygroup.user.dto

data class UpdateProfileDto(
    val major: String? = null,
    val nickname: String? = null,
    val profileImageUrl: String? = null,
    val bio: String? = null,
)
