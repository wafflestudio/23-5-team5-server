package com.team5.studygroup

import com.fasterxml.jackson.databind.ObjectMapper
import com.team5.studygroup.helper.DataGenerator
import com.team5.studygroup.user.repository.UserRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class ProfileIntegrationTest
    @Autowired
    constructor(
        private val mvc: MockMvc,
        private val mapper: ObjectMapper,
        private val generator: DataGenerator,
        private val userRepository: UserRepository,
    ) {
        @Test
        fun `should update profile successfully without image`() {
            val (user, token) = generator.generateUser()

            val newNickname = "updatedNick"
            val newMajor = "Industrial Engineering"
            val newBio = "Hello, I am updated!"

            mvc.perform(
                multipart("/api/users/me")
                    .param("nickname", newNickname)
                    .param("major", newMajor)
                    .param("bio", newBio)
                    .header("Authorization", "Bearer $token")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .with { request ->
                        request.method = "PATCH"
                        request
                    },
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.nickname").value(newNickname))
                .andExpect(jsonPath("$.major").value(newMajor))
                .andExpect(jsonPath("$.bio").value(newBio))
        }

        @Test
        fun `should return 409 when updating with duplicated nickname`() {
            val (userA, _) = generator.generateUser()
            val targetNickname = userA.nickname

            val (userB, tokenB) = generator.generateUser()

            mvc.perform(
                multipart("/api/users/me")
                    .param("nickname", targetNickname)
                    .header("Authorization", "Bearer $tokenB")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .with { request ->
                        request.method = "PATCH"
                        request
                    },
            )
                .andExpect(status().isConflict)
        }

        @Test
        fun `should get profile successfully`() {
            val (user, token) = generator.generateUser()

            mvc.perform(
                get("/api/users/me")
                    .header("Authorization", "Bearer $token"),
            )
                .andExpect(status().isOk) // 200 OK
                .andExpect(jsonPath("$.username").value(user.username))
                .andExpect(jsonPath("$.nickname").value(user.nickname))
                .andExpect(jsonPath("$.major").value(user.major))
                .andExpect(jsonPath("$.student_number").value(user.studentNumber))
                .andExpect(jsonPath("$.role").value(user.userRole.description)) // Role 검증
        }
    }
