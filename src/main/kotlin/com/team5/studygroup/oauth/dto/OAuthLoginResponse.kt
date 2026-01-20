package com.team5.studygroup.oauth.dto

data class OAuthLoginResponse(
    // "LOGIN" or "REGISTER"
    val type: String,
    // AccessToken or RegisterToken
    val token: String,
)
