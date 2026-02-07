package com.team5.studygroup.review.repository

import com.team5.studygroup.review.model.Review
import com.team5.studygroup.user.model.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
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

    @Query("""
        SELECT r FROM Review r 
        WHERE r.revieweeId = :revieweeId 
        AND (:cursorId IS NULL OR r.id < :cursorId) 
        ORDER BY r.id DESC
    """)
    fun findByRevieweeIdAndCursor(
        @Param("revieweeId") revieweeId: Long,
        @Param("cursorId") cursorId: Long?,
        pageable: Pageable
    ): List<Review>
}
