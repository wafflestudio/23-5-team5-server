package com.team5.studygroup.verification.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class VerifyRequest(
    @JsonProperty("register_token")
    val registerToken: String,
    val email: String,
    val code: String,
)
