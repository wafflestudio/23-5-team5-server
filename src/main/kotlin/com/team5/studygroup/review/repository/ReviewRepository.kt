package com.team5.studygroup.review.repository

import com.team5.studygroup.review.model.Review
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ReviewRepository : JpaRepository<Review, Long> {
    fun findByGroupId(
        @Param("groupId") group_id: Long,
        pageable: Pageable,
    ): Page<Review>

    fun findByReviewerId(
        @Param("reviewerId") reviewer_id: Long,
        pageable: Pageable,
    ): Page<Review>

    fun findByRevieweeId(
        @Param("revieweeId") reviewee_id: Long,
        pageable: Pageable,
    ): Page<Review>
}
