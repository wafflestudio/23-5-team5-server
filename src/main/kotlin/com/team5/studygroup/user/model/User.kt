package com.team5.studygroup.user.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
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
    @Column(nullable = true)
    var password: String? = null,
    // 학과
    @Column(nullable = false)
    var major: String,
    // 학번
    @Column(nullable = false, unique = true)
    var studentNumber: String,
    // 닉네임
    @Column(nullable = false, unique = true)
    var nickname: String,
    // 인증 여부
    @Column(nullable = false)
    var isVerified: Boolean = false,
    // 프로필 이미지 경로
    @Column(length = 512, nullable = true)
    var profileImageUrl: String? = null,
    // 자기소개
    @Column(columnDefinition = "TEXT", nullable = true)
    var bio: String? = null,
    // 권한
    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false)
    var userRole: Role = Role.USER,
    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdAt: Instant? = null,
    @LastModifiedDate
    @Column(nullable = false)
    var updatedAt: Instant? = null,
) {
    fun updateProfile(
        major: String?,
        nickname: String?,
        profileImageUrl: String?,
        bio: String?,
    ) {
        // 파라미터가 null이면 기존 값(this.xxx)을 그대로 유지합니다.
        this.major = major ?: this.major
        this.nickname = nickname ?: this.nickname
        this.profileImageUrl = profileImageUrl ?: this.profileImageUrl
        this.bio = bio ?: this.bio
    }

    fun updateProfile(profileImageUrl: String?) {
        this.profileImageUrl = profileImageUrl ?: this.profileImageUrl
    }

    fun isAdmin(): Boolean = this.userRole == Role.ADMIN
}
