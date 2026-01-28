package com.team5.studygroup.review.dto

data class CreateReviewDto(
    val group_id: Long,
    val reviewer_id: Long,
    val reviewee_id: Long,
    val description: String,
)
