package com.team5.studygroup.group.dto

data class CreateGroupDto(
    val groupName: String,
    val description: String,
    val categoryId: Long,
    val subCategoryId: Long,
    val capacity: Int? = null,
    val leaderId: Long? = null,
    val isOnline: Boolean,
    val location: String,
    val status: String,
)
