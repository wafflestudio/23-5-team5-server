package com.team5.studygroup.review.service

import com.team5.studygroup.common.CursorResponse
import com.team5.studygroup.review.dto.CreateReviewDto
import com.team5.studygroup.review.dto.DeleteReviewDto
import com.team5.studygroup.review.dto.ReviewResponse
import com.team5.studygroup.review.dto.UpdateReviewDto
import com.team5.studygroup.review.exception.ReviewDeleteForbiddenException
import com.team5.studygroup.review.exception.ReviewNotFoundException
import com.team5.studygroup.review.exception.ReviewUpdateForbiddenException
import com.team5.studygroup.review.model.Review
import com.team5.studygroup.review.repository.ReviewRepository
import com.team5.studygroup.user.UserNotFoundException
import com.team5.studygroup.user.repository.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReviewService(
    private val reviewRepository: ReviewRepository,
    private val userRepository: UserRepository,
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

    @Transactional(readOnly = true)
    fun searchReviews(
        revieweeId: Long,
        cursorId: Long?,
        size: Int,
    ): CursorResponse<ReviewResponse> {
        val reviewee =
            userRepository.findById(revieweeId).orElseThrow {
                UserNotFoundException()
            }

        val pageable = PageRequest.of(0, size + 1)

        val reviews = reviewRepository.findByRevieweeIdAndCursor(revieweeId, cursorId, pageable)

        return makeCursorResponse(reviews, size)
    }

    private fun makeCursorResponse(
        reviews: List<Review>,
        size: Int,
    ): CursorResponse<ReviewResponse> {
        var hasNext = false
        val resultList = ArrayList(reviews)

        if (resultList.size > size) {
            hasNext = true
            resultList.removeAt(size)
        }

        val nextCursorId = resultList.lastOrNull()?.id

        val content = resultList.map { ReviewResponse.from(it) }

        return CursorResponse(
            content = content,
            nextCursorId = nextCursorId,
            hasNext = hasNext,
        )
    }
}
