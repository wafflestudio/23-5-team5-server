package com.team5.studygroup.review.repository

import com.team5.studygroup.review.model.Review
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ReviewRepository: JpaRepository<Review, Long> {
    fun findByRevieweeId (
        @Param("revieweeId") reviewee_id: Long,
    ): List<Review>
}