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
    @Column(nullable = true)
    var password: String? = null,
    @Column(nullable = false, unique = true)
    var email: String,
    // 학과
    @Column(nullable = true)
    var major: String? = null,
    // 학번
    @Column(nullable = true, unique = true)
    var studentNumber: String? = null,
    // 닉네임
    @Column(nullable = true, unique = true)
    var nickname: String? = null,
    // 인증 여부
    @Column(nullable = true)
    var isVerified: Boolean? = false,
    // 프로필 이미지 경로
    @Column(length = 512, nullable = true)
    var profileImageUrl: String? = null,
    // 자기소개
    @Column(columnDefinition = "TEXT", nullable = true)
    var bio: String? = null,
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

    // 최초 등록용: 학번까지 포함
    fun registerProfile(
        studentNumber: String,
        major: String,
        nickname: String,
        profileImageUrl: String?,
        bio: String?,
    ) {
        this.studentNumber = studentNumber
        this.major = major
        this.nickname = nickname
        this.profileImageUrl = profileImageUrl
        this.bio = bio
    }
}
