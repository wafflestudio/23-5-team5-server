package com.team5.studygroup.review.service

import com.team5.studygroup.review.dto.CreateReviewDto
import com.team5.studygroup.review.dto.DeleteReviewDto
import com.team5.studygroup.review.dto.UpdateReviewDto
import com.team5.studygroup.review.model.Review
import com.team5.studygroup.review.repository.ReviewRepository

class ReviewService(
    private val reviewRepository: ReviewRepository,
) {
    fun createReview(
        createReviewDto: CreateReviewDto,
        userId: Long,
    ): Review {
        val review = Review(
            groupId = createReviewDto.group_id,
            reviewerId = userId,
            revieweeId = createReviewDto.reviewee_id,
            description = createReviewDto.description,
        )
        return reviewRepository.save(review)
    }

    fun deleteReview (
        deleteReviewDto: DeleteReviewDto,
        userId: Long,
    ) {
        reviewRepository.deleteById(deleteReviewDto.reviewId)
    }

    fun UpdateReview (
        updateReviewDto: UpdateReviewDto,
        userId: Long,
    ): Review {
        val review = reviewRepository.findById(updateReviewDto.reviewId).orElseThrow()
        review.UpdateReview(updateReviewDto.description)
        return reviewRepository.save(review)
    }

    fun SearchReviews (
        revieweeId: Long,
    ): List<Review> {
        return reviewRepository.findByRevieweeId(revieweeId)
    }
}