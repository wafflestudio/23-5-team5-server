package com.team5.studygroup.user.dto

import com.team5.studygroup.user.model.User
import java.time.Instant

data class GetProfileDto(
    val userId: Long?,
    val username: String,
    val email: String,
    val major: String?,
    val studentNumber: String?,
    val nickname: String?,
    val profileImageUrl: String?,
    val bio: String?,
    val createdAt: Instant?,
) {
    companion object {
        // User 엔티티를 받아서 GetProfileDto로 변환해주는 함수
        fun fromEntity(user: User): GetProfileDto {
            return GetProfileDto(
                userId = user.id,
                username = user.username,
                email = user.email,
                major = user.major,
                studentNumber = user.studentNumber,
                nickname = user.nickname,
                profileImageUrl = user.profileImageUrl,
                bio = user.bio,
                createdAt = user.createdAt,
            )
        }
    }
}
