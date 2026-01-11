package com.team5.studygroup.user.model

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

@Entity // 2. JPA 엔티티임을 선언 (필수)
@Table(name = "users")
@EntityListeners(AuditingEntityListener::class)
class User(
    @Id // 3. 반드시 jakarta.persistence.Id여야 합니다!
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(nullable = false, unique = true)
    var username: String,
    @Column(nullable = false)
    var password: String,
    @Column(nullable = false, unique = true)
    var email: String,
    @CreatedDate
    @Column(updatable = false)
    var createdAt: Instant? = null,
    @LastModifiedDate
    var updatedAt: Instant? = null,
)
