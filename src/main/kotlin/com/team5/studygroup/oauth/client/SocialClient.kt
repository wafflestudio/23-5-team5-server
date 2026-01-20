package com.team5.studygroup.oauth.client

import com.team5.studygroup.oauth.dto.OAuthInfo
import com.team5.studygroup.user.model.ProviderType

interface SocialClient {
    // 소셜 타입 ex. google, kakao...
    fun supportServer(): ProviderType

    // 토큰으로 표준화된 info 받기
    fun fetchMemberInfo(token: String): OAuthInfo
}
