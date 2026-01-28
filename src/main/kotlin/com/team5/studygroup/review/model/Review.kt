package com.team5.studygroup.review.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@Entity
@Table(name = "reviews")
@EntityListeners(AuditingEntityListener::class)
class Review(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    val groupId: Long,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id")
    val reviewerId: Long,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewee_id")
    val revieweeId: Long,
    @Column(columnDefinition = "TEXT")
    var description: String?,
    @CreatedDate
    @Column(updatable = false)
    var createdAt: Instant? = null,
    @LastModifiedDate
    var updatedAt: Instant? = null,
) {
    fun updateReview(description: String) {
        this.description = description
    }
}
