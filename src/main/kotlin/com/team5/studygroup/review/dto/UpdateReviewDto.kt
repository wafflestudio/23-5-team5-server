package com.team5.studygroup.review.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class UpdateReviewDto(
    @JsonProperty("review_id")
    val reviewId: Long,
    val description: String,
)
