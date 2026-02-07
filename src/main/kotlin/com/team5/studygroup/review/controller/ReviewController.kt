package com.team5.studygroup.review.controller

import com.team5.studygroup.common.CursorResponse
import com.team5.studygroup.review.dto.CreateReviewDto
import com.team5.studygroup.review.dto.DeleteReviewDto
import com.team5.studygroup.review.dto.ReviewResponse
import com.team5.studygroup.review.dto.UpdateReviewDto
import com.team5.studygroup.review.model.Review
import com.team5.studygroup.review.service.ReviewService
import com.team5.studygroup.user.LoggedInUser
import com.team5.studygroup.user.dto.UserSearchResponseDto
import io.swagger.v3.oas.annotations.Parameter
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/reviews")
class ReviewController(
    private val reviewService: ReviewService,
) {
    @PostMapping("")
    fun createReview(
        @RequestBody createReviewDto: CreateReviewDto,
        @Parameter(hidden=true) @LoggedInUser userId: Long,
    ): ResponseEntity<Review> {
        return ResponseEntity.ok(reviewService.createReview(createReviewDto, userId))
    }

    @DeleteMapping("")
    fun deleteReview(
        @RequestBody deleteReviewDto: DeleteReviewDto,
        @Parameter(hidden=true) @LoggedInUser userId: Long,
    ): ResponseEntity<Unit> {
        return ResponseEntity.ok(reviewService.deleteReview(deleteReviewDto, userId))
    }

    @PatchMapping("")
    fun updateReview(
        @RequestBody updateReviewDto: UpdateReviewDto,
        @Parameter(hidden=true) @LoggedInUser userId: Long,
    ): ResponseEntity<Review> {
        return ResponseEntity.ok(reviewService.updateReview(updateReviewDto, userId))
    }

    @GetMapping("/search")
    fun search(
        @Parameter(hidden = true) @LoggedInUser requestingUserId: Long,
        @RequestParam("revieweeId", required = true) revieweeId: Long,
        @Parameter(description = "커서 ID (이전 페이지의 마지막 리뷰 ID)") @RequestParam(required = false) cursorId: Long?,
        @Parameter(description = "페이지 사이즈 (1~50), 기본값은 10입니다.") @RequestParam(defaultValue = "10") @Min(1) @Max(50) size: Int,
    ): ResponseEntity<CursorResponse<ReviewResponse>> {
        val result = reviewService.searchReviews(revieweeId, cursorId, size)
        return ResponseEntity.ok(result)
    }
}
