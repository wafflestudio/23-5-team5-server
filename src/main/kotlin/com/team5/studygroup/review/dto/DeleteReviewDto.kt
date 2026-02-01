package com.team5.studygroup.review.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class DeleteReviewDto(
    @JsonProperty("review_id")
    val reviewId: Long,
)
