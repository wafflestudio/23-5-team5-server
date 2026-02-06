package com.team5.studygroup.user.dto

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

data class UserTargetDto(
    @Schema(description = "조회할 대상 유저의 ID", example = "12")
    @field:JsonProperty("user_id")
    val userId: Long
)