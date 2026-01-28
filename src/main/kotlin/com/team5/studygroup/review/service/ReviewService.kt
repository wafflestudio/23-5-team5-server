package com.team5.studygroup.review.service

import com.team5.studygroup.review.dto.CreateReviewDto
import com.team5.studygroup.review.dto.DeleteReviewDto
import com.team5.studygroup.review.dto.ReviewResponse
import com.team5.studygroup.review.dto.UpdateReviewDto
import com.team5.studygroup.review.exception.ReviewDeleteForbiddenException
import com.team5.studygroup.review.exception.ReviewNotFoundException
import com.team5.studygroup.review.exception.ReviewUpdateForbiddenException
import com.team5.studygroup.review.model.Review
import com.team5.studygroup.review.repository.ReviewRepository

class ReviewService(
    private val reviewRepository: ReviewRepository,
) {
    fun createReview(
        createReviewDto: CreateReviewDto,
        userId: Long,
    ): Review {
        val review =
            Review(
                groupId = createReviewDto.group_id,
                reviewerId = userId,
                revieweeId = createReviewDto.reviewee_id,
                description = createReviewDto.description,
            )
        return reviewRepository.save(review)
    }

    fun deleteReview(
        deleteReviewDto: DeleteReviewDto,
        userId: Long,
    ) {
        val review =
            reviewRepository.findById(deleteReviewDto.reviewId).orElseThrow {
                ReviewNotFoundException()
            }
        if (review.reviewerId != userId) {
            throw ReviewDeleteForbiddenException()
        }
        reviewRepository.deleteById(deleteReviewDto.reviewId)
    }

    fun updateReview(
        updateReviewDto: UpdateReviewDto,
        userId: Long,
    ): Review {
        val review =
            reviewRepository.findById(updateReviewDto.reviewId).orElseThrow {
                ReviewNotFoundException()
            }
        if (review.reviewerId != userId) {
            throw ReviewUpdateForbiddenException()
        }

        review.updateReview(updateReviewDto.description)
        return reviewRepository.save(review)
    }

    fun search(
        groupId: Long?,
        reviewerId: Long?,
        revieweeId: Long?,
    ): List<ReviewResponse> {
        if (groupId != null) {
            return reviewRepository.findByGroupId(groupId).map { ReviewResponse.from(it) }
        }
        if (reviewerId != null) {
            return reviewRepository.findByReviewerId(reviewerId).map { ReviewResponse.from(it) }
        }
        if (revieweeId != null) {
            return reviewRepository.findByRevieweeId(revieweeId).map { ReviewResponse.from(it) }
        }
        return listOf()
    }
}
