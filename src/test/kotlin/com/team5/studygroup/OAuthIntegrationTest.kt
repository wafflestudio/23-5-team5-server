package com.team5.studygroup

import com.fasterxml.jackson.databind.ObjectMapper
import com.team5.studygroup.helper.DataGenerator
import com.team5.studygroup.jwt.JwtTokenProvider
import com.team5.studygroup.oauth.client.SocialClientComposite
import com.team5.studygroup.oauth.dto.OAuthInfo
import com.team5.studygroup.oauth.dto.OAuthSignUpRequest
import com.team5.studygroup.oauth.dto.SocialLoginRequest
import com.team5.studygroup.user.model.ProviderType
import com.team5.studygroup.user.repository.SocialAuthRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
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
class OAuthIntegrationTest
    @Autowired
    constructor(
        private val mvc: MockMvc,
        private val mapper: ObjectMapper,
        private val generator: DataGenerator,
        private val socialAuthRepository: SocialAuthRepository,
        private val jwtTokenProvider: JwtTokenProvider,
    ) {
        @MockitoBean
        lateinit var socialClientComposite: SocialClientComposite

        @Test
        fun `should oauth login successfully when already oauth login`() {
            val token = "google-token"
            val myProviderId = "google-unique-id-99"
            val myEmail = "waffle@snu.ac.kr"
            `when`(socialClientComposite.fetch(ProviderType.GOOGLE, token))
                .thenReturn(OAuthInfo(providerId = myProviderId, email = myEmail))
            val (_, _) = generator.generateSocialAuth(ProviderType.GOOGLE, myProviderId)
            val request = SocialLoginRequest(token)
            mvc.perform(
                post("/api/oauth/login/google")
                    .content(mapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)
                .andExpect(jsonPath("$.type").value("LOGIN"))
                .andExpect(jsonPath("$.token").exists())
        }

        @Test
        fun `should oauth login successfully when email ends with snu and already registered`() {
            val token = "google-token"
            val myProviderId = "google-unique-id-99"
            val myEmail = "waffle@snu.ac.kr"
            `when`(socialClientComposite.fetch(ProviderType.GOOGLE, token))
                .thenReturn(OAuthInfo(providerId = myProviderId, email = myEmail))
            val (user, _) = generator.generateUser(username = myEmail)
            val request = SocialLoginRequest(token)
            mvc.perform(
                post("/api/oauth/login/google")
                    .content(mapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)
                .andExpect(jsonPath("$.type").value("LOGIN"))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isNotEmpty)

            val socialAuth = socialAuthRepository.findByProviderAndProviderId(ProviderType.GOOGLE, myProviderId)
            assertThat(socialAuth).isNotNull
            assertThat(socialAuth?.user?.id).isEqualTo(user.id)
        }

        @Test
        fun `should oauth login successfully with signUp needed for snu email`() {
            val token = "google-token"
            val myProviderId = "google-unique-id-99"
            val myEmail = "waffle@snu.ac.kr"
            `when`(socialClientComposite.fetch(ProviderType.GOOGLE, token))
                .thenReturn(OAuthInfo(providerId = myProviderId, email = myEmail))
            val request = SocialLoginRequest(token)
            mvc.perform(
                post("/api/oauth/login/google")
                    .content(mapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)
                .andExpect(jsonPath("$.type").value("REGISTER"))
        }

        @Test
        fun `should oauth login successfully with signUp needed for none snu email`() {
            val token = "google-token"
            val myProviderId = "google-unique-id-99"
            val myEmail = "waffle@gmail.com"
            `when`(socialClientComposite.fetch(ProviderType.GOOGLE, token))
                .thenReturn(OAuthInfo(providerId = myProviderId, email = myEmail))
            val request = SocialLoginRequest(token)
            mvc.perform(
                post("/api/oauth/login/google")
                    .content(mapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)
                .andExpect(jsonPath("$.type").value("REGISTER"))
        }

        @Test
        fun `should return 400 when wrong provider entered`() {
            val token = "google-token"
            val myProviderId = "google-unique-id-99"
            val myEmail = "waffle@snu.ac.kr"
            `when`(socialClientComposite.fetch(ProviderType.GOOGLE, token))
                .thenReturn(OAuthInfo(providerId = myProviderId, email = myEmail))
            val (user, _) = generator.generateUser(username = myEmail)
            val request = SocialLoginRequest(token)
            mvc.perform(
                post("/api/oauth/login/invalidProvider")
                    .content(mapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isBadRequest)
        }

        @Test
        fun `snu email should oauth signUp successfully with access token provided`() {
            val myProviderId = "google-unique-id-99"
            val myEmail = "waffle@snu.ac.kr"

            val registerToken = jwtTokenProvider.createRegisterToken("GOOGLE", myProviderId, myEmail)

            val request =
                OAuthSignUpRequest(
                    registerToken = registerToken,
                    major = "Computer Science",
                    studentNumber = "2024-12345",
                    nickname = "nickname1234",
                )
            mvc.perform(
                post("/api/oauth/signUp/google")
                    .content(mapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.username").value(myEmail))
                .andExpect(jsonPath("$.is_verified").value(true))
        }

        @Test
        fun `none snu email should oauth signUp successfully with access token provided`() {
            val myProviderId = "google-unique-id-99"
            val myEmail = "waffle@gmail.com"

            val registerToken = jwtTokenProvider.createRegisterToken("GOOGLE", myProviderId, myEmail)

            val request =
                OAuthSignUpRequest(
                    registerToken = registerToken,
                    email = "waffle@snu.ac.kr",
                    major = "Computer Science",
                    studentNumber = "2024-12345",
                    nickname = "nickname1234",
                )
            mvc.perform(
                post("/api/oauth/signUp/google")
                    .content(mapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.username").value("waffle@snu.ac.kr"))
                .andExpect(jsonPath("$.is_verified").value(true))
        }

        @Test
        fun `should return 400 when wrong type token provided`() {
            val myEmail = "waffle@snu.ac.kr"

            val registerToken = jwtTokenProvider.createAccessToken(myEmail)
            val request =
                OAuthSignUpRequest(
                    registerToken = registerToken,
                    major = "Computer Science",
                    studentNumber = "2024-12345",
                    nickname = "nickname1234",
                )
            mvc.perform(
                post("/api/oauth/signUp/google")
                    .content(mapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isBadRequest)
        }

        @Test
        fun `should return 400 when provider mismatch`() {
            val myProviderId = "google-unique-id-99"
            val myEmail = "waffle@snu.ac.kr"

            val registerToken = jwtTokenProvider.createRegisterToken("KAKAO", myProviderId, myEmail)
            val request =
                OAuthSignUpRequest(
                    registerToken = registerToken,
                    major = "Computer Science",
                    studentNumber = "2024-12345",
                    nickname = "nickname1234",
                )
            mvc.perform(
                post("/api/oauth/signUp/google")
                    .content(mapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isBadRequest)
        }

        @Test
        fun `should return 401 when token is invalid`() {
            val registerToken = "InvalidRegisterToken"
            val request =
                OAuthSignUpRequest(
                    registerToken = registerToken,
                    major = "Computer Science",
                    studentNumber = "2024-12345",
                    nickname = "nickname1234",
                )
            mvc.perform(
                post("/api/oauth/signUp/google")
                    .content(mapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isUnauthorized)
        }

        @Test
        fun `should return 403 when invalid email provided`() {
            val myProviderId = "google-unique-id-99"
            val myEmail = "waffle@gmail.com"

            val registerToken = jwtTokenProvider.createRegisterToken("GOOGLE", myProviderId, myEmail)
            val request =
                OAuthSignUpRequest(
                    registerToken = registerToken,
                    email = "waffle@gmail.com",
                    major = "Computer Science",
                    studentNumber = "2024-12345",
                    nickname = "nickname1234",
                )
            mvc.perform(
                post("/api/oauth/signUp/google")
                    .content(mapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON),
            ).andExpect(status().isForbidden)
        }
    }
