package com.team5.studygroup.usergroup.service

import com.team5.studygroup.group.repository.GroupRepository
import com.team5.studygroup.usergroup.GroupNotFoundException
import com.team5.studygroup.usergroup.UserGroupDuplicateException
import com.team5.studygroup.usergroup.UserGroupNotFoundException
import com.team5.studygroup.usergroup.dto.JoinGroupDto
import com.team5.studygroup.usergroup.dto.WithdrawGroupDto
import com.team5.studygroup.usergroup.model.UserGroup
import com.team5.studygroup.usergroup.repository.UserGroupRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class UserGroupService(
    private val userGroupRepository: UserGroupRepository,
    private val groupRepository: GroupRepository,
) {
    @Transactional
    fun joinGroup(
        joinGroupDto: JoinGroupDto,
        requestingUserId: Long,
    ) {
        if (!groupRepository.existsById(joinGroupDto.groupId)) {
            throw GroupNotFoundException()
        }

        if (userGroupRepository.findByGroupIdAndUserId(joinGroupDto.groupId, requestingUserId) != null) {
            throw UserGroupDuplicateException()
        }

        userGroupRepository.save(
            UserGroup(
                groupId = joinGroupDto.groupId,
                userId = requestingUserId,
            ),
        )
    }

    @Transactional
    fun withdrawGroup(
        withdrawGroupDto: WithdrawGroupDto,
        requestingUserId: Long,
    ) {
        val userGroup =
            userGroupRepository.findByGroupIdAndUserId(withdrawGroupDto.groupId, requestingUserId)
                ?: throw UserGroupNotFoundException()

        userGroupRepository.delete(userGroup)
    }
}
