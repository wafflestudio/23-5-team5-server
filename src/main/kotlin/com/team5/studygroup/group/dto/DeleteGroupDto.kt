package com.team5.studygroup.group.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class DeleteGroupDto(
    @JsonProperty("group_id")
    val groupId: Long,
)
