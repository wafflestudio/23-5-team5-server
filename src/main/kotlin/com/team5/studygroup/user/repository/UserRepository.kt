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
                email = rs.getString("email"),
                password = rs.getString("password"),
                createdAt = rs.getTimestamp("created_at")?.toInstant(),
                updatedAt = rs.getTimestamp("updated_at")?.toInstant(),
            )
        }

    fun save(user: User): Int {
        return jdbcTemplate.update(
            "INSERT INTO users (username, email, password, created_at, updated_at) VALUES (?, ?, ?, NOW(), NOW())",
            user.username,
            user.email,
            user.password,
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
