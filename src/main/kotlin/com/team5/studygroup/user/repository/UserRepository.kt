package com.team5.studygroup.user.repository

import com.team5.studygroup.user.model.User
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

@Repository
class UserRepository(private val jdbcTemplate: JdbcTemplate) {
    private val rowMapper =
        RowMapper { rs, _ ->
            User(
                id = rs.getLong("id"),
                username = rs.getString("username"),
                password = rs.getString("password"),
                email = rs.getString("email"),
                major = rs.getString("major"),
                studentNumber = rs.getString("student_number"),
                nickname = rs.getString("nickname"),
                isVerified = rs.getBoolean("is_verified"),
                profileImageUrl = rs.getString("profile_image_url"),
                bio = rs.getString("bio"),
                createdAt = rs.getTimestamp("created_at")?.toInstant(),
                updatedAt = rs.getTimestamp("updated_at")?.toInstant(),
            )
        }

    fun save(user: User): Int {
        return jdbcTemplate.update(
            """
            INSERT INTO users (
                username, password, email, major, student_number, 
                nickname, is_verified, profile_image_url, bio, created_at, updated_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())
            """.trimIndent(),
            user.username,
            user.password,
            user.email,
            user.major,
            user.studentNumber,
            user.nickname,
            user.isVerified,
            user.profileImageUrl,
            user.bio,
        )
    }

    fun findByEmail(email: String): User? {
        val sql = "SELECT * FROM users WHERE email = ?"
        return jdbcTemplate.query(sql, rowMapper, email).firstOrNull()
    }

    fun findByUsername(username: String): User? {
        val sql = "SELECT * FROM users WHERE username = ?"
        return jdbcTemplate.query(sql, rowMapper, username).firstOrNull()
    }
}
