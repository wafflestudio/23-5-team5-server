// src/test/kotlin/com/team5/studygroup/OAuthIntegrationTest.kt

package com.team5.studygroup

import com.fasterxml.jackson.databind.ObjectMapper
import com.team5.studygroup.helper.mock.MockRedis
import com.team5.studygroup.oauth.client.SocialClientComposite
import com.team5.studygroup.oauth.dto.OAuthInfo
import com.team5.studygroup.oauth.dto.SocialLoginRequest
import com.team5.studygroup.user.dto.SignUpDto
import com.team5.studygroup.user.model.ProviderType
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@AutoConfigureMockMvc
@ContextConfiguration(initializers = [MockRedis::class])
class OAuthIntegrationTest
    @Autowired
    constructor(
        private val mvc: MockMvc,
        private val mapper: ObjectMapper,
    ) {
        // 소셜 API 호출을 Mock으로 대체
        @MockitoBean
        private lateinit var socialClientComposite: SocialClientComposite

        @Test
        fun `소셜 로그인 - 신규 유저는 REGISTER 반환`() {
            // Mock: 카카오에서 유저 정보 반환
            `when`(socialClientComposite.fetch(ProviderType.KAKAO, "fake_kakao_token"))
                .thenReturn(
                    OAuthInfo(
                        providerId = "kakao_123456",
                        email = "newuser@gmail.com",
                    ),
                )

            val request = SocialLoginRequest(token = "fake_kakao_token")

            mvc.perform(
                post("/api/oauth/login/kakao")
                    .content(mapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON),
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.status").value("REGISTER"))
                .andExpect(jsonPath("$.token").exists())
        }

        @Test
        fun `소셜 로그인 - SNU 이메일 기존 유저는 LOGIN 반환`() {
            // 먼저 일반 회원가입으로 유저 생성
            val signUpRequest =
                SignUpDto(
                    username = "existing@snu.ac.kr",
                    password = "password123",
                    nickname = "기존유저",
                    studentNumber = "2024-88888",
                    major = "컴퓨터공학",
                )
            mvc.perform(
                post("/api/auth/signup")
                    .content(mapper.writeValueAsString(signUpRequest))
                    .contentType(MediaType.APPLICATION_JSON),
            )

            // Mock: 카카오에서 같은 SNU 이메일 반환
            `when`(socialClientComposite.fetch(ProviderType.KAKAO, "fake_kakao_token"))
                .thenReturn(
                    OAuthInfo(
                        providerId = "kakao_789",
                        email = "existing@snu.ac.kr",
                    ),
                )

            val request = SocialLoginRequest(token = "fake_kakao_token")

            mvc.perform(
                post("/api/oauth/login/kakao")
                    .content(mapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON),
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.status").value("LOGIN"))
                .andExpect(jsonPath("$.token").exists())
        }

        @Test
        fun `소셜 로그인 - 지원하지 않는 provider는 실패`() {
            val request = SocialLoginRequest(token = "some_token")

            mvc.perform(
                post("/api/oauth/login/facebook")
                    .content(mapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isBadRequest)
        }

        @Test
        fun `구글 소셜 로그인 - 신규 유저`() {
            // Mock: 구글에서 유저 정보 반환
            `when`(socialClientComposite.fetch(ProviderType.GOOGLE, "fake_google_token"))
                .thenReturn(
                    OAuthInfo(
                        providerId = "google_123456",
                        email = "newuser@gmail.com",
                    ),
                )

            val request = SocialLoginRequest(token = "fake_google_token")

            mvc.perform(
                post("/api/oauth/login/google")
                    .content(mapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON),
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.status").value("REGISTER"))
        }
    }
