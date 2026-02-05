package com.team5.studygroup.usergroup.service

import com.team5.studygroup.group.GroupStatus
import com.team5.studygroup.group.repository.GroupRepository
import com.team5.studygroup.usergroup.GroupFullException
import com.team5.studygroup.usergroup.GroupLeaderCannotJoinException
import com.team5.studygroup.usergroup.GroupNotFoundException
import com.team5.studygroup.usergroup.GroupNotRecruitingException
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
        val group =
            groupRepository.findByIdWithLock(joinGroupDto.groupId)
                .orElseThrow { GroupNotFoundException() }

        if (group.status != GroupStatus.RECRUITING) {
            throw GroupNotRecruitingException()
        }

        if (group.leaderId != null && group.leaderId == requestingUserId) {
            throw GroupLeaderCannotJoinException()
        }

        val maxCapacity = group.capacity

        if (maxCapacity != null) {
            val currentMemberCount = userGroupRepository.countByGroupId(joinGroupDto.groupId)
            if (currentMemberCount >= maxCapacity) {
                throw GroupFullException()
            }
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
        val group =
            groupRepository.findById(withdrawGroupDto.groupId)
                .orElseThrow { GroupNotFoundException() }

        val userGroup =
            userGroupRepository.findByGroupIdAndUserId(withdrawGroupDto.groupId, requestingUserId)
                ?: throw UserGroupNotFoundException()

        userGroupRepository.delete(userGroup)
    }
}
