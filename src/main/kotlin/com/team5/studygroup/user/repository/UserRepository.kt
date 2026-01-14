package com.team5.studygroup.user.repository

import com.team5.studygroup.user.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    // 닉네임이나 이메일로 유저를 찾는 쿼리는 함수 이름만 정해주면 JPA가 알아서 SQL을 만듭니다.
    fun findByEmail(email: String): User?

    fun findByUsername(username: String): User?

    fun findByNickname(nickname: String): User?

    @Query(
        """
        SELECT u.*
        FROM users u
        JOIN user_groups ug ON ug.user_id = u.id
        WHERE ug.group_id = ?1
    """,
        nativeQuery = true,
    )
    fun findUsersInGroup(groupId: Long): List<User>
}
