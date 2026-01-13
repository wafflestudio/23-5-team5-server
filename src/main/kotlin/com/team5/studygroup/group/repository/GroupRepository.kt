package com.team5.studygroup.group.repository

import com.team5.studygroup.group.model.Group
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface GroupRepository: JpaRepository<Group, Long> {
    fun findByLeaderId(leaderId: Long): List<Group>
    fun findByCategoryId(categoryId: Long): List<Group>
    fun findBySubCategoryId(subCategoryId: Long): List<Group>
    fun findByStatus(status: String): List<Group>

    @Query("""
        SELECT g.*
        FROM groups g
        JOIN user_groups ug ON ug.group_id = g.id
        WHERE ug.user_id = ?1
    """, nativeQuery = true)
    fun findGroupsByUser(userId: Long): List<Group>
}