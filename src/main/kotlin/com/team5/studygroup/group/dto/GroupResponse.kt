package com.team5.studygroup.group.dto

import com.team5.studygroup.group.GroupStatus
import com.team5.studygroup.group.model.Group
import java.time.Instant

data class GroupResponse(
    val id: Long,
    val groupName: String,
    val description: String,
    val categoryId: Long,
    val subCategoryId: Long,
    val capacity: Int?,
    val leaderId: Long?,
    val isOnline: Boolean,
    val location: String,
    val status: GroupStatus,
    val createdAt: Instant?,
) {
    companion object {
        fun from(group: Group) =
            GroupResponse(
                id = group.id!!,
                groupName = group.groupName,
                description = group.description,
                categoryId = group.categoryId,
                subCategoryId = group.subCategoryId,
                capacity = group.capacity,
                leaderId = group.leaderId,
                isOnline = group.isOnline,
                location = group.location,
                status = group.status,
                createdAt = group.createdAt,
            )
    }
}
