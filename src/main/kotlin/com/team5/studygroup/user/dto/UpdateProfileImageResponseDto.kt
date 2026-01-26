package com.team5.studygroup.user.dto

import java.time.Instant

data class UpdateProfileImageResponseDto(
    val profileImageUrl: String?,
    val createdAt: Instant?,
)
