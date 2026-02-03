package com.team5.studygroup.usergroup.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class WithdrawGroupDto(
    @JsonProperty("group_id")
    val groupId: Long,
)
