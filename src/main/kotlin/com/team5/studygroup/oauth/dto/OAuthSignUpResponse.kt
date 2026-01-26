package com.team5.studygroup.oauth.dto

data class OAuthSignUpResponse(
    val accessToken: String,
    val username: String,
    val nickname: String,
    val isVerified: Boolean,
)