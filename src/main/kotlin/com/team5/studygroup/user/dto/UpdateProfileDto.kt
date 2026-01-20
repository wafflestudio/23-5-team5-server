package com.team5.studygroup.user.dto

import org.springframework.web.multipart.MultipartFile

data class UpdateProfileDto(
    val major: String? = null,
    val nickname: String? = null,
    val profileImage: MultipartFile? = null,
    val bio: String? = null,
)
