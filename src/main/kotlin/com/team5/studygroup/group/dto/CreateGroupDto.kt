package com.team5.studygroup.group.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class CreateGroupDto(
    @JsonProperty("group_name")
    val groupName: String,
    val description: String,
    @JsonProperty("category_id")
    val categoryId: Long,
    @JsonProperty("sub_category_id")
    val subCategoryId: Long,
    val capacity: Int? = null,
    @JsonProperty("is_online")
    val isOnline: Boolean,
    val location: String,
)
