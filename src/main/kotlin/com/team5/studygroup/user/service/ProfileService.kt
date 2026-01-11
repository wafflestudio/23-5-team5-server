package com.team5.studygroup.user.service

import org.springframework.stereotype.Service

@Service
class ProfileService {
    fun postProfile(
        userId: Long,
        major: String,
        nickname: String,
        profileImageUrl: String,
        bio: String,
    ): String {
        return "내 정보 등록"
    }

    fun updateProfile(
        userId: Long,
        major: String,
        nickname: String,
        profileImageUrl: String,
        bio: String,
    ): String {
        return "내 정보 수정"
    }

    fun getProfile(userId: Long): String {
        return "내 정보 조회"
    }
}
