package com.team5.studygroup.user.service

import com.team5.studygroup.group.repository.GroupRepository
import com.team5.studygroup.user.UserSearchNotAllowedException
import com.team5.studygroup.user.dto.UserSearchResponseDto
import com.team5.studygroup.user.repository.UserRepository
import com.team5.studygroup.usergroup.GroupNotFoundException
import com.team5.studygroup.usergroup.repository.UserGroupRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
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
        pageable: Pageable,
    ): Page<UserSearchResponseDto> {
        val group = groupRepository.findById(groupId).orElseThrow { GroupNotFoundException() }
        // 그룹장만 접근 가능
        if (requestingUserId != group.leaderId) {
            throw UserSearchNotAllowedException()
        }
        val userGroups = userGroupRepository.findByGroupId(groupId)
        val users = userRepository.findByIdIn(userGroups.map { it.userId }, pageable)
        return users.map { UserSearchResponseDto.from(it) }
    }
}
