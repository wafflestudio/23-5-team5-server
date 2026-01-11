package com.team5.studygroup.usergroup.service

import com.team5.studygroup.usergroup.dto.JoinGroupDto
import com.team5.studygroup.usergroup.dto.WithdrawGroupDto
import org.springframework.stereotype.Service

@Service
class UserGroupService {
    fun joinGroup(joinGroupDto: JoinGroupDto): String {
        return "그룹 가입 완료"
    }

    fun withdrawGroup(withdrawGroupDto: WithdrawGroupDto): String {
        return "그룹 탈퇴 완료"
    }
}
