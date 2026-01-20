package com.team5.studygroup.oauth.client

import com.team5.studygroup.oauth.dto.OAuthInfo
import com.team5.studygroup.user.model.ProviderType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class GoogleClient(
    private val restTemplate: RestTemplate,
) : SocialClient {
    override fun supportServer() = ProviderType.GOOGLE

    override fun fetchMemberInfo(token: String): OAuthInfo {
        val googleApiUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=$token"

        try {
            // 구글 서버에 GET 요청
            // 결과는 JSON으로 받음
            val response =
                restTemplate.getForObject(googleApiUrl, Map::class.java)
                    ?: throw RuntimeException("구글 응답값이 비어있습니다.")

            val id =
                response["sub"] as? String
                    ?: throw RuntimeException("Google response has no 'sub'")

            val email =
                response["email"] as? String
                    ?: throw RuntimeException("Google response has no 'email'")

            val name = response["name"] as? String

            return OAuthInfo(
                email = email,
                providerId = id,
            )
        } catch (e: Exception) {
            throw RuntimeException("유효하지 않은 구글 토큰입니다.", e)
        }
    }
}
