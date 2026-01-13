package com.team5.studygroup.usergroup.repository

import com.team5.studygroup.usergroup.model.UserGroup
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserGroupRepository: JpaRepository<UserGroup, Long> {
    fun findByGroupId(groupId: Long): List<UserGroup>
    fun findByUserId(userId: Long): List<UserGroup>
    fun findByGroupIdAndUserId(groupId: Long, userId: Long): UserGroup?
}