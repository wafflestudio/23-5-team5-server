package com.team5.studygroup.user.dto

import com.team5.studygroup.user.model.User

data class UserSearchResponseDto(
    val id: Long?,
    val username: String,
    val major: String,
    val nickname: String,
) {
    companion object {
        fun from(user: User): UserSearchResponseDto {
            return UserSearchResponseDto(
                id = user.id,
                username = user.username,
                major = user.major,
                nickname = user.nickname,
            )
        }
    }
}
