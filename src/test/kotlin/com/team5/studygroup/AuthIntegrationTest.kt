package com.team5.studygroup

import com.fasterxml.jackson.databind.ObjectMapper
import com.team5.studygroup.helper.DataGenerator
import com.team5.studygroup.user.dto.LoginDto
import com.team5.studygroup.user.dto.SignUpDto
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class AuthIntegrationTest
    @Autowired
    constructor(
        private val mvc: MockMvc,
        private val mapper: ObjectMapper,
        private val generator: DataGenerator,
    ) {
        @Test
        fun `should register successfully`() {
            val request =
                SignUpDto(
                    username = "user1234",
                    password = "password1234",
                    major = "computer science",
                    studentNumber = "2024-12345",
                    nickname = "nickname1234",
                )

            mvc.perform(
                post("/api/auth/signup")
                    .content(mapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)
        }

        @Test
        fun `should return 409 error when username duplicated`() {
            val existingRequest =
                SignUpDto(
                    username = "duplicateUser",
                    password = "password1",
                    major = "math",
                    studentNumber = "2023-00001",
                    nickname = "nick1",
                )
            mvc.perform(
                post("/api/auth/signup")
                    .content(mapper.writeValueAsString(existingRequest))
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)

            val newRequest =
                SignUpDto(
                    username = "duplicateUser",
                    password = "password2",
                    major = "science",
                    studentNumber = "2023-00002",
                    nickname = "nick2",
                )

            mvc.perform(
                post("/api/auth/signup")
                    .content(mapper.writeValueAsString(newRequest))
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isConflict)
        }

        @Test
        fun `should return 409 error when student number duplicated`() {
            val existingRequest =
                SignUpDto(
                    username = "uniqueUser1",
                    password = "password1",
                    major = "math",
                    studentNumber = "2023-99999",
                    nickname = "nickA",
                )
            mvc.perform(
                post("/api/auth/signup")
                    .content(mapper.writeValueAsString(existingRequest))
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)

            val newRequest =
                SignUpDto(
                    username = "uniqueUser2",
                    password = "password2",
                    major = "art",
                    studentNumber = "2023-99999",
                    nickname = "nickB",
                )

            mvc.perform(
                post("/api/auth/signup")
                    .content(mapper.writeValueAsString(newRequest))
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isConflict)
        }

        @Test
        fun `should return 409 error when nickname duplicated`() {
            val existingRequest =
                SignUpDto(
                    username = "uniqueUser3",
                    password = "password1",
                    major = "math",
                    studentNumber = "2023-11111",
                    nickname = "SuperStar",
                )
            mvc.perform(
                post("/api/auth/signup")
                    .content(mapper.writeValueAsString(existingRequest))
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)

            val newRequest =
                SignUpDto(
                    username = "uniqueUser4",
                    password = "password2",
                    major = "history",
                    studentNumber = "2023-22222",
                    nickname = "SuperStar",
                )

            mvc.perform(
                post("/api/auth/signup")
                    .content(mapper.writeValueAsString(newRequest))
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isConflict)
        }

        @Test
        fun `should login successfully`() {
            val password = "qwer1234"
            val (user, _) = generator.generateUser(password = password)
            val request =
                LoginDto(
                    username = user.username,
                    password = password,
                )
            mvc
                .perform(
                    post("/api/auth/login")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON),
                ).andExpect(status().isOk)
        }

        @Test
        fun `should return 400 when username is incorrect during login`() {
            val password = "qwer1234"
            val (_, _) = generator.generateUser(password = password)
            val request =
                LoginDto(
                    username = "wrong-username",
                    password = password,
                )
            mvc
                .perform(
                    post("/api/auth/login")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON),
                ).andExpect(status().isUnauthorized)
        }

        @Test
        fun `should return 400 when password is incorrect during login`() {
            val (user, _) = generator.generateUser()
            val request =
                LoginDto(
                    username = user.username,
                    password = "wrong-password",
                )
            mvc
                .perform(
                    post("/api/auth/login")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON),
                ).andExpect(status().isUnauthorized)
        }
    }
