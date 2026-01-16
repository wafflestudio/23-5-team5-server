package com.team5.studygroup.usergroup.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "user_study_groups")
class UserGroup(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "group_id", nullable = false)
    val groupId: Long,
    @Column(name = "user_id", nullable = false)
    val userId: Long,
    @Column(nullable = false)
    val status: String,
)
