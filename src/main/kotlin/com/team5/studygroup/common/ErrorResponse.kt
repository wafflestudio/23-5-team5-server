package com.team5.studygroup.common

import java.time.LocalDateTime

data class ErrorResponse(
    val errorCode: Int,
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
)
