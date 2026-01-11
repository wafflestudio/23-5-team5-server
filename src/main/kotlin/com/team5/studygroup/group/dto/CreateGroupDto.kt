package com.team5.studygroup.group.dto

import org.springframework.data.annotation.Id

data class CreateGroupDto(
    val groupName: String,
    val description: String,
    val categoryId: Int,
    val subCategoryId: Int,
    val capacity: Int,
    val leaderId: Long? = null,
    val isOnline: Boolean,
    val status: String,
)
