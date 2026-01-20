package com.team5.studygroup.user.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class UpdateProfileDto(
    val major: String? = null,
    val nickname: String? = null,
    @JsonProperty("profile_image_url")
    val profileImageUrl: String? = null,
    val bio: String? = null,
)
