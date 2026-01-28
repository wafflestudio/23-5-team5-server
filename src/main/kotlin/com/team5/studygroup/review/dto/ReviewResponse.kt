package com.team5.studygroup.review.dto

import com.team5.studygroup.review.model.Review
import java.time.Instant

data class ReviewResponse(
    val id: Long,
    val groupId: Long,
    val reviewerId: Long,
    val revieweeId: Long,
    val description: String?,
    val createdAt: Instant?,
) {
    companion object {
        fun from(review: Review) =
            ReviewResponse(
                id = review.id!!,
                groupId = review.groupId,
                reviewerId = review.reviewerId,
                revieweeId = review.revieweeId,
                description = review.description,
                createdAt = review.createdAt,
            )
    }
}
