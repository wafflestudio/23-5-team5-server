package com.team5.studygroup.group.repository

import com.team5.studygroup.group.model.Group
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface GroupRepository : JpaRepository<Group, Long> {
    fun findByIdIn(ids: List<Long>, pageable: Pageable): Page<Group>

    fun findByLeaderId(
        leaderId: Long,
        pageable: Pageable,
    ): Page<Group>

    fun findByCategoryId(
        categoryId: Long,
        pageable: Pageable,
    ): Page<Group>

    fun findBySubCategoryId(
        subCategoryId: Long,
        pageable: Pageable,
    ): Page<Group>

    fun findByGroupNameContainingOrDescriptionContaining(
        groupName: String,
        description: String,
        pageable: Pageable,
    ): Page<Group>

    // 카테고리 + 키워드 동시 검색
    @Query(
        """
        SELECT g FROM Group g 
        WHERE g.categoryId = :categoryId 
        AND (g.groupName LIKE %:keyword% OR g.description LIKE %:keyword%)
    """,
    )
    fun findByCategoryIdAndKeyword(
        @Param("categoryId") categoryId: Long,
        @Param("keyword") keyword: String,
        pageable: Pageable,
    ): Page<Group>

    // 서브카테고리 + 키워드 검색
    @Query(
        """
    SELECT g FROM Group g 
    WHERE g.subCategoryId = :subCategoryId 
    AND (g.groupName LIKE %:keyword% OR g.description LIKE %:keyword%)
""",
    )
    fun findBySubCategoryIdAndKeyword(
        @Param("subCategoryId") subCategoryId: Long,
        @Param("keyword") keyword: String,
        pageable: Pageable,
    ): Page<Group>
}
