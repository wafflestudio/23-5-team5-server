package com.team5.studygroup.oauth.client

import com.team5.studygroup.oauth.dto.OAuthInfo
import com.team5.studygroup.user.model.ProviderType
import org.springframework.stereotype.Component

@Component
class SocialClientComposite(
    socialClients: Set<SocialClient>,
) {
    private val clientMap: Map<ProviderType, SocialClient> =
        socialClients.associateBy { it.supportServer() }

    fun fetch(
        provider: ProviderType,
        token: String,
    ): OAuthInfo {
        val client =
            clientMap[provider]
                ?: throw IllegalArgumentException("지원하지 않는 소셜 타입입니다: $provider")

        return client.fetchMemberInfo(token)
    }
}
