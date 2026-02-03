package com.team5.studygroup.usergroup.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class JoinGroupDto(
    @JsonProperty("group_id")
    val groupId: Long,
)
