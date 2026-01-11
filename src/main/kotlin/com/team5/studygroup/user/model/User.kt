package com.team5.studygroup.user.model

import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import java.time.Instant

@Table(name = "Users")
class User(
    @Id val id: Long? = null,
    val username: String,
    val password: String,
    val email: String,
    val major: String,
    val studentNumber: String,
    val nickname: String,
    val isVerified: Boolean,
    val profileImageUrl: String,
    val bio: String,
    @CreatedDate
    val createdAt: Instant? = null,
    @LastModifiedDate
    val updatedAt: Instant? = null,
)
