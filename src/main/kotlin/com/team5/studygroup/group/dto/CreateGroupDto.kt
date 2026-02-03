package com.team5.studygroup.group.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CreateGroupDto(
    @field:NotBlank(message = "그룹 이름은 필수입니다.")
    @JsonProperty("group_name")
    val groupName: String,
    @field:NotBlank(message = "소개글은 필수입니다.")
    val description: String,
    @field:NotNull(message = "카테고리 ID는 필수입니다.")
    @JsonProperty("category_id")
    val categoryId: Long,
    @field:NotNull(message = "서브 카테고리 ID는 필수입니다.")
    @JsonProperty("sub_category_id")
    val subCategoryId: Long,
    val capacity: Int? = null,
    @JsonProperty("is_online")
    val isOnline: Boolean = false,
    @field:NotBlank(message = "장소는 필수입니다.")
    val location: String,
)
