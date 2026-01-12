package com.team5.studygroup.group.model

import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import java.time.Instant

@Table(name = "groups")
class Group(
    @Id val id: Long? = null,
    val groupName: String,
    val description: String,
    val categoryId: Int,
    val subCategoryId: Int,
    val capacity: Int,
    val leaderId: Long? = null,
    val isOnline: Boolean,
    val status: String,
    @CreatedDate
    val createdAt: Instant? = null,
    @LastModifiedDate
    val updatedAt: Instant? = null,
)
