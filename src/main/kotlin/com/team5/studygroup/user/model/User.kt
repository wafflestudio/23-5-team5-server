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
    @CreatedDate
    val createdAt: Instant? = null,
    @LastModifiedDate
    val updatedAt: Instant? = null,

    )