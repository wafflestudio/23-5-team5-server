package com.team5.studygroup.usergroup.model

import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.springframework.data.annotation.Id

@Entity
@Table(name = "user_groups")
class UserGroup(
    @Id val id: Long? = null,
    val groupId: Long,
    val userId: Long,
    val status: String,
)
