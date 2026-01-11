package com.team5.studygroup.usergroup.model

import org.springframework.data.annotation.Id

class UserGroup(
    @Id val id: Long? = null,
    val groupId: Long,
    val userId: Long,
    val status: String,
)
