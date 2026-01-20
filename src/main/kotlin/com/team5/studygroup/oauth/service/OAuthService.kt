package com.team5.studygroup.oauth.service

import com.team5.studygroup.jwt.JwtTokenProvider
import com.team5.studygroup.oauth.client.SocialClientComposite
import com.team5.studygroup.oauth.dto.OAuthLoginResponse
import com.team5.studygroup.user.model.ProviderType
import com.team5.studygroup.user.model.SocialAuth
import com.team5.studygroup.user.repository.SocialAuthRepository
import com.team5.studygroup.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class OAuthService(
    private val socialClientComposite: SocialClientComposite,
    private val socialAuthRepository: SocialAuthRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val userRepository: UserRepository,
) {
    fun login(
        provider: ProviderType,
        token: String,
    ): OAuthLoginResponse {
        val info = socialClientComposite.fetch(provider, token)
        val socialAuth = socialAuthRepository.findByProviderAndProviderId(provider, info.providerId)
        if (socialAuth != null) {
            val token = jwtTokenProvider.createAccessToken(socialAuth.user.username)
            return OAuthLoginResponse("LOGIN", token)
        }
        val user = userRepository.findByUsername(info.email)
        val endsWithSnu = info.email.endsWith("snu.ac.kr")
        if (endsWithSnu && user != null) {
            val newSocialAuth =
                SocialAuth(
                    user = user!!,
                    provider = provider,
                    providerId = info.providerId,
                )
            socialAuthRepository.save(newSocialAuth)
            return OAuthLoginResponse("LOGIN", token = jwtTokenProvider.createAccessToken(user.username))
        }

        return OAuthLoginResponse(
            "REGISTER",
            token = jwtTokenProvider.createRegisterToken(provider.toString(), info.providerId, info.email),
        )
    }
}
