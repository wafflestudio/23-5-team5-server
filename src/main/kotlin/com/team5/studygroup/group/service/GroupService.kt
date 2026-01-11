package com.team5.studygroup.group.service

import com.team5.studygroup.group.dto.CreateGroupDto
import com.team5.studygroup.group.dto.DeleteGroupDto
import com.team5.studygroup.group.dto.ExpireGroupDto

class GroupService {
    fun createGroup(createGroupDto: CreateGroupDto): String {
        return "그룹 생성 완료"
    }

    fun deleteGroup(deleteGroupDto: DeleteGroupDto): String {
        return "그룹 삭제 완료"
    }

    fun expireGroup(expireGroupDto: ExpireGroupDto): String {
        return "그룹 마감 완료"
    }
}
