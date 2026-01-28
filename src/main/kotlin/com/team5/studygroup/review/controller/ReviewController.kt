package com.team5.studygroup.review.controller

import com.team5.studygroup.review.dto.CreateReviewDto
import com.team5.studygroup.review.dto.DeleteReviewDto
import com.team5.studygroup.review.dto.ReviewResponse
import com.team5.studygroup.review.dto.UpdateReviewDto
import com.team5.studygroup.review.model.Review
import com.team5.studygroup.review.service.ReviewService
import com.team5.studygroup.user.LoggedInUser
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
        @LoggedInUser userId: Long,
    ): ResponseEntity<Review> {
        return ResponseEntity.ok(reviewService.createReview(createReviewDto, userId))
    }

    @DeleteMapping("")
    fun deleteReview(
        @RequestBody DeleteReviewDto: DeleteReviewDto,
        @LoggedInUser userId: Long,
    ): ResponseEntity<Unit> {
        return ResponseEntity.ok(reviewService.deleteReview(DeleteReviewDto, userId))
    }

    @PatchMapping("")
    fun updateReview(
        @RequestBody UpdateReviewDto: UpdateReviewDto,
        @LoggedInUser userId: Long,
    ): ResponseEntity<Review> {
        return ResponseEntity.ok(reviewService.updateReview(UpdateReviewDto, userId))
    }

    @GetMapping("/search")
    fun search(
        @RequestParam("groupId", required = false) groupId: Long?,
        @RequestParam("reviewerId", required = false) reviewerId: Long?,
        @RequestParam("revieweeId", required = false) revieweeId: Long?,
    ): ResponseEntity<List<ReviewResponse>> {
        val result = reviewService.search(groupId, reviewerId, revieweeId)
        return ResponseEntity.ok(result)
    }
}
