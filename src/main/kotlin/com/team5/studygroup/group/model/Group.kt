package com.team5.studygroup.group.model

import com.team5.studygroup.group.GroupStatus
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
@Table(name = "study_groups")
@EntityListeners(AuditingEntityListener::class) // 생성/수정일 자동 기록을 위해 필요
class Group(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(nullable = false)
    val groupName: String,
    @Column(columnDefinition = "TEXT")
    val description: String,
    val categoryId: Long,
    val subCategoryId: Long,
    val capacity: Int? = null,
    val leaderId: Long? = null,
    val isOnline: Boolean,
    val location: String,
    var status: GroupStatus,
    @CreatedDate
    @Column(updatable = false)
    val createdAt: Instant? = null,
    @LastModifiedDate
    val updatedAt: Instant? = null,
) {
    fun expire() {
        this.status = GroupStatus.EXPIRED
    }
}
