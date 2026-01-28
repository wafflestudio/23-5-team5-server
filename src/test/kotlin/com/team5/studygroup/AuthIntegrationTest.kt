// src/test/kotlin/com/team5/studygroup/AuthIntegrationTest.kt

package com.team5.studygroup

import com.fasterxml.jackson.databind.ObjectMapper
import com.team5.studygroup.helper.mock.MockRedis
import com.team5.studygroup.user.dto.LoginDto
import com.team5.studygroup.user.dto.SignUpDto
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@AutoConfigureMockMvc
@ContextConfiguration(initializers = [MockRedis::class])
class AuthIntegrationTest
    @Autowired
    constructor(
        private val mvc: MockMvc,
        private val mapper: ObjectMapper,
    ) {
        // ========== 회원가입 테스트 ==========

        @Test
        fun `회원가입 성공`() {
            val request =
                SignUpDto(
                    username = "test@snu.ac.kr",
                    password = "password123",
                    nickname = "테스트유저",
                    studentNumber = "2024-12345",
                    major = "컴퓨터공학",
                )

            mvc.perform(
                post("/api/auth/signup")
                    .content(mapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON),
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.nickname").value("테스트유저"))
        }

        @Test
        fun `회원가입 실패 - 중복된 아이디`() {
            val request =
                SignUpDto(
                    username = "duplicate@snu.ac.kr",
                    password = "password123",
                    nickname = "유저1",
                    studentNumber = "2024-11111",
                    major = "컴퓨터공학",
                )

            // 첫 번째 가입
            mvc.perform(
                post("/api/auth/signup")
                    .content(mapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)

            // 같은 아이디로 다시 가입 시도
            val duplicateRequest =
                SignUpDto(
                    username = "duplicate@snu.ac.kr",
                    password = "password123",
                    nickname = "유저2",
                    studentNumber = "2024-22222",
                    major = "컴퓨터공학",
                )

            mvc.perform(
                post("/api/auth/signup")
                    .content(mapper.writeValueAsString(duplicateRequest))
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isBadRequest)
        }

        @Test
        fun `회원가입 실패 - 중복된 닉네임`() {
            val request1 =
                SignUpDto(
                    username = "user1@snu.ac.kr",
                    password = "password123",
                    nickname = "중복닉네임",
                    studentNumber = "2024-33333",
                    major = "컴퓨터공학",
                )

            mvc.perform(
                post("/api/auth/signup")
                    .content(mapper.writeValueAsString(request1))
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)

            val request2 =
                SignUpDto(
                    username = "user2@snu.ac.kr",
                    password = "password123",
                    nickname = "중복닉네임",
                    studentNumber = "2024-44444",
                    major = "전기공학",
                )

            mvc.perform(
                post("/api/auth/signup")
                    .content(mapper.writeValueAsString(request2))
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isBadRequest)
        }

        // ========== 로그인 테스트 ==========

        @Test
        fun `로그인 성공`() {
            // 먼저 회원가입
            val signUpRequest =
                SignUpDto(
                    username = "login@snu.ac.kr",
                    password = "password123",
                    nickname = "로그인유저",
                    studentNumber = "2024-55555",
                    major = "컴퓨터공학",
                )
            mvc.perform(
                post("/api/auth/signup")
                    .content(mapper.writeValueAsString(signUpRequest))
                    .contentType(MediaType.APPLICATION_JSON),
            )

            // 로그인
            val loginRequest =
                LoginDto(
                    username = "login@snu.ac.kr",
                    password = "password123",
                )
            mvc.perform(
                post("/api/auth/login")
                    .content(mapper.writeValueAsString(loginRequest))
                    .contentType(MediaType.APPLICATION_JSON),
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.accessToken").exists())
        }

        @Test
        fun `로그인 실패 - 잘못된 비밀번호`() {
            // 먼저 회원가입
            val signUpRequest =
                SignUpDto(
                    username = "wrongpw@snu.ac.kr",
                    password = "password123",
                    nickname = "비밀번호틀림",
                    studentNumber = "2024-66666",
                    major = "컴퓨터공학",
                )
            mvc.perform(
                post("/api/auth/signup")
                    .content(mapper.writeValueAsString(signUpRequest))
                    .contentType(MediaType.APPLICATION_JSON),
            )

            // 틀린 비밀번호로 로그인
            val loginRequest =
                LoginDto(
                    username = "wrongpw@snu.ac.kr",
                    password = "wrongpassword",
                )
            mvc.perform(
                post("/api/auth/login")
                    .content(mapper.writeValueAsString(loginRequest))
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isUnauthorized)
        }

        @Test
        fun `로그인 실패 - 존재하지 않는 아이디`() {
            val loginRequest =
                LoginDto(
                    username = "notexist@snu.ac.kr",
                    password = "password123",
                )
            mvc.perform(
                post("/api/auth/login")
                    .content(mapper.writeValueAsString(loginRequest))
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isUnauthorized)
        }

        // ========== 토큰 인증 테스트 ==========

        @Test
        fun `유효한 토큰으로 프로필 조회 성공`() {
            // 회원가입해서 토큰 받기
            val signUpRequest =
                SignUpDto(
                    username = "profile@snu.ac.kr",
                    password = "password123",
                    nickname = "프로필유저",
                    studentNumber = "2024-77777",
                    major = "컴퓨터공학",
                )
            val result =
                mvc.perform(
                    post("/api/auth/signup")
                        .content(mapper.writeValueAsString(signUpRequest))
                        .contentType(MediaType.APPLICATION_JSON),
                ).andReturn()

            val response = mapper.readTree(result.response.contentAsString)
            val token = response.get("accessToken").asText()

            // 토큰으로 프로필 조회
            mvc.perform(
                get("/api/users/profile")
                    .header("Authorization", "Bearer $token"),
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.nickname").value("프로필유저"))
        }

        @Test
        fun `유효하지 않은 토큰으로 프로필 조회 실패`() {
            mvc.perform(
                get("/api/users/profile")
                    .header("Authorization", "Bearer invalid_token"),
            ).andExpect(status().isUnauthorized)
        }

        @Test
        fun `토큰 없이 프로필 조회 실패`() {
            mvc.perform(
                get("/api/users/profile"),
            ).andExpect(status().isUnauthorized)
        }
    }
