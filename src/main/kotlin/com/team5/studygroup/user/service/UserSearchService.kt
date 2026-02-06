package com.team5.studygroup.user.service

import com.team5.studygroup.common.CursorResponse
import com.team5.studygroup.group.repository.GroupRepository
import com.team5.studygroup.user.UserNotFoundException
import com.team5.studygroup.user.UserSearchNotAllowedException
import com.team5.studygroup.user.dto.GetProfileDto
import com.team5.studygroup.user.dto.UserSearchResponseDto
import com.team5.studygroup.user.model.User
import com.team5.studygroup.user.repository.UserRepository
import com.team5.studygroup.usergroup.GroupNotFoundException
import com.team5.studygroup.usergroup.repository.UserGroupRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class UserSearchService(
    private val userRepository: UserRepository,
    private val userGroupRepository: UserGroupRepository,
    private val groupRepository: GroupRepository,
) {
    fun searchUsersInGroup(
        groupId: Long,
        requestingUserId: Long,
        cursorId: Long?,
        size: Int,
    ): CursorResponse<UserSearchResponseDto> {
        val group = groupRepository.findById(groupId).orElseThrow { GroupNotFoundException() }
        // 그룹장만 접근 가능
        if (requestingUserId != group.leaderId) {
            throw UserSearchNotAllowedException()
        }
        val userGroups = userGroupRepository.findByGroupId(groupId)
        val userIds = userGroups.map { it.userId }

        if (userIds.isEmpty()) {
            return CursorResponse(emptyList(), null, false)
        }

        val pageable = PageRequest.of(0, size + 1)
        val users = userRepository.findByIdInAndCursor(userIds, cursorId, pageable)

        return makeCursorResponse(users, size)
    }

    private fun makeCursorResponse(
        users: List<User>,
        size: Int,
    ): CursorResponse<UserSearchResponseDto> {
        var hasNext = false
        val resultList = ArrayList(users)

        if (resultList.size > size) {
            hasNext = true
            resultList.removeAt(size)
        }

        val nextCursorId = resultList.lastOrNull()?.id

        return CursorResponse(
            content = resultList.map { UserSearchResponseDto.from(it) },
            nextCursorId = nextCursorId,
            hasNext = hasNext,
        )
    }

    fun getOtherProfile(targetUserId: Long): GetProfileDto {
        val user = userRepository.findById(targetUserId)
            .orElseThrow { UserNotFoundException() }

        return GetProfileDto.fromEntity(user)
    }
}
