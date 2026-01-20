package com.team5.studygroup.oauth.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class OAuthInfo(
    val email: String,
    @JsonProperty("provider_id")
    val providerId: String,
)
