package com.team5.studygroup.group.repository

import com.team5.studygroup.group.model.Group
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface GroupRepository : JpaRepository<Group, Long> {
    /**
     * 통합 검색 쿼리 (카테고리, 서브카테고리, 키워드, 커서)
     * 파라미터가 NULL이면 해당 조건은 무시(IS NULL)하고, 값이 있으면 필터링합니다.
     */
    @Query(
        """
        SELECT g FROM Group g
        WHERE (:categoryId IS NULL OR g.categoryId = :categoryId)
        AND (:subCategoryId IS NULL OR g.subCategoryId = :subCategoryId)
        AND (:keyword IS NULL OR (g.groupName LIKE :keyword OR g.description LIKE :keyword))
        AND (:cursorId IS NULL OR g.id < :cursorId)
        ORDER BY g.id DESC
    """,
    )
    fun searchWithConditions(
        @Param("categoryId") categoryId: Long?,
        @Param("subCategoryId") subCategoryId: Long?,
        @Param("keyword") keyword: String?,
        @Param("cursorId") cursorId: Long?,
        pageable: Pageable,
    ): List<Group>

    // 내가 작성한 공고 (리더 ID 기준)
    @Query("SELECT g FROM Group g WHERE g.leaderId = :leaderId AND (:cursorId IS NULL OR g.id < :cursorId) ORDER BY g.id DESC")
    fun findByLeaderIdAndCursor(
        @Param("leaderId") leaderId: Long,
        @Param("cursorId") cursorId: Long?,
        pageable: Pageable,
    ): List<Group>

    // 내가 참여한 그룹 (ID 목록 IN절)
    @Query("SELECT g FROM Group g WHERE g.id IN :ids AND (:cursorId IS NULL OR g.id < :cursorId) ORDER BY g.id DESC")
    fun findByIdInAndCursor(
        @Param("ids") ids: List<Long>,
        @Param("cursorId") cursorId: Long?,
        pageable: Pageable,
    ): List<Group>
}
