package com.team5.studygroup.group.service

import com.team5.studygroup.group.GroupStatus
import com.team5.studygroup.group.dto.CreateGroupDto
import com.team5.studygroup.group.dto.DeleteGroupDto
import com.team5.studygroup.group.dto.ExpireGroupDto
import com.team5.studygroup.group.dto.GroupSearchResponse
import com.team5.studygroup.group.exception.GroupAlreadyExpiredException
import com.team5.studygroup.group.exception.GroupNotFoundException
import com.team5.studygroup.group.exception.NotGroupLeaderException
import com.team5.studygroup.group.model.Group
import com.team5.studygroup.group.repository.GroupRepository
import com.team5.studygroup.user.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GroupService(
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository,
) {
    @Transactional
    fun createGroup(
        createGroupDto: CreateGroupDto,
        requestingUserId: Long,
    ): GroupSearchResponse {
        val leader =
            userRepository.findByIdOrNull(requestingUserId)
                ?: throw IllegalArgumentException("유저를 찾을 수 없습니다.")

        val savedGroup =
            groupRepository.save(
                Group(
                    groupName = createGroupDto.groupName,
                    description = createGroupDto.description,
                    categoryId = createGroupDto.categoryId,
                    subCategoryId = createGroupDto.subCategoryId,
                    capacity = createGroupDto.capacity,
                    leaderId = requestingUserId,
                    isOnline = createGroupDto.isOnline,
                    location = createGroupDto.location,
                    status = GroupStatus.RECRUITING,
                ),
            )
        return GroupSearchResponse.from(savedGroup, leader)
    }

    @Transactional
    fun deleteGroup(
        deleteGroupDto: DeleteGroupDto,
        requestingUserId: Long,
    ) {
        val group =
            groupRepository.findByIdOrNull(deleteGroupDto.groupId)
                ?: throw GroupNotFoundException()

        // 삭제를 요청한 사용자가 그룹장인 경우에만 허용
        if (group.leaderId != requestingUserId) {
            throw NotGroupLeaderException()
        }

        groupRepository.delete(group)
    }

    @Transactional
    fun expireGroup(
        expireGroupDto: ExpireGroupDto,
        requestingUserId: Long,
    ) {
        val group =
            groupRepository.findByIdOrNull(expireGroupDto.groupId)
                ?: throw GroupNotFoundException()

        // 만료를 요청한 사용자가 그룹장인 경우에만 허용
        if (group.leaderId != requestingUserId) {
            throw NotGroupLeaderException()
        }

        // 이미 만료된 그룹은 다시 만료시킬 수 없음
        if (group.status == GroupStatus.EXPIRED) {
            throw GroupAlreadyExpiredException()
        }

        group.expire()
    }
}
