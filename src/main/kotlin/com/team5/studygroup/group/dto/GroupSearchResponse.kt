package com.team5.studygroup.group.dto

import com.team5.studygroup.group.GroupStatus
import com.team5.studygroup.group.model.Group
import com.team5.studygroup.user.model.User
import java.time.Instant

data class GroupSearchResponse(
    val id: Long,
    val groupName: String,
    val description: String,
    val categoryId: Long,
    val subCategoryId: Long,
    val capacity: Int?,
    val leaderId: Long?,
    val leaderNickname: String?,
    val leaderBio: String?,
    val leaderUserName: String?,
    val leaderProfileImageUrl: String?,
    val isOnline: Boolean,
    val location: String,
    val status: GroupStatus,
    val createdAt: Instant?,
) {
    companion object {
        fun from(
            group: Group,
            leader: User?,
        ) = GroupSearchResponse(
            id = group.id!!,
            groupName = group.groupName,
            description = group.description,
            categoryId = group.categoryId,
            subCategoryId = group.subCategoryId,
            capacity = group.capacity,
            leaderId = group.leaderId,
            leaderNickname = leader?.nickname,
            leaderBio = leader?.bio,
            leaderUserName = leader?.username,
            leaderProfileImageUrl = leader?.profileImageUrl,
            isOnline = group.isOnline,
            location = group.location,
            status = group.status,
            createdAt = group.createdAt,
        )
    }
}
