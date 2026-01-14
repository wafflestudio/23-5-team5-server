package com.team5.studygroup.group.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@Entity
@Table(name = "groups")
@EntityListeners(AuditingEntityListener::class) // 생성/수정일 자동 기록을 위해 필요
class Group(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val groupName: String,
    val description: String,
    val categoryId: Int,
    val subCategoryId: Int,
    val capacity: Int,
    val leaderId: Long? = null,
    val isOnline: Boolean,
    val status: String,
    @CreatedDate
    @Column(updatable = false)
    val createdAt: Instant? = null,
    @LastModifiedDate
    val updatedAt: Instant? = null,
)
