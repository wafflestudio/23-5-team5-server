package com.team5.studygroup

import com.fasterxml.jackson.databind.ObjectMapper
import com.team5.studygroup.common.RedisService
import com.team5.studygroup.helper.DataGenerator
import com.team5.studygroup.jwt.JwtTokenProvider
import com.team5.studygroup.verification.dto.SocialVerifyRequest
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class VerificationIntegrationTest @Autowired constructor(
    private val mvc: MockMvc,
    private val mapper: ObjectMapper,
    private val generator: DataGenerator,
    private val jwtTokenProvider: JwtTokenProvider
) {

    @MockitoBean
    private lateinit var redisService: RedisService

    @Test
    fun `should return LOGIN type when user already exists`() {
        val email = "waffle@snu.ac.kr"
        val code = "123456"
        val provider = "GOOGLE"
        val providerId = "google-id-123"

        given(redisService.getData(email)).willReturn(code)

        val (user, _) = generator.generateUser(username = email)

        val registerToken = jwtTokenProvider.createRegisterToken(provider, providerId, email)
        val request = SocialVerifyRequest(registerToken, email, code)

        mvc.perform(
            post("/api/auth/social/verify")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.type").value("LOGIN"))
            .andExpect(jsonPath("$.token").exists())
    }

    @Test
    fun `should return REGISTER type when user does not exist`() {
        // Given
        val email = "newbie@snu.ac.kr"
        val code = "654321"
        val provider = "KAKAO"
        val providerId = "kakao-id-123"

        given(redisService.getData(email)).willReturn(code)

        val registerToken = jwtTokenProvider.createRegisterToken(provider, providerId, email)
        val request = SocialVerifyRequest(registerToken, email, code)

        mvc.perform(
            post("/api/auth/social/verify")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.type").value("REGISTER"))
            .andExpect(jsonPath("$.token").exists())
    }

    @Test
    fun `should return 400 Bad Request when verification code is incorrect`() {
        val email = "hacker@snu.ac.kr"
        val wrongCode = "000000"
        val realCode = "123456"
        val provider = "GOOGLE"
        val providerId = "hacker-id"

        given(redisService.getData(email)).willReturn(realCode)

        val registerToken = jwtTokenProvider.createRegisterToken(provider, providerId, email)

        val request = SocialVerifyRequest(registerToken, email, wrongCode)

        mvc.perform(
            post("/api/auth/social/verify")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
    }
}