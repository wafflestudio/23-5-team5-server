package com.team5.studygroup.oauth.service

import com.team5.studygroup.jwt.JwtTokenProvider
import com.team5.studygroup.oauth.OAuthException.InvalidRegisterToken
import com.team5.studygroup.oauth.OAuthException.InvalidTokenSubject
import com.team5.studygroup.oauth.OAuthException.ProviderMismatch
import com.team5.studygroup.oauth.OAuthException.InvalidEmailDomain
import com.team5.studygroup.oauth.client.SocialClientComposite
import com.team5.studygroup.oauth.dto.OAuthLoginResponse
import com.team5.studygroup.oauth.dto.OAuthSignUpRequest
import com.team5.studygroup.oauth.dto.OAuthSignUpResponse
import com.team5.studygroup.user.model.ProviderType
import com.team5.studygroup.user.model.Role
import com.team5.studygroup.user.model.SocialAuth
import com.team5.studygroup.user.model.User
import com.team5.studygroup.user.repository.SocialAuthRepository
import com.team5.studygroup.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

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
                    user = user,
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

    @Transactional
    fun signUp(
        provider: ProviderType,
        request: OAuthSignUpRequest,
    ): OAuthSignUpResponse {

        val claims = try {
            jwtTokenProvider.getRegisterClaims(request.registerToken)
        } catch (e: Exception) {
            throw InvalidRegisterToken()
        }

        val tokenProvider = claims["provider"] as String
        val tokenProviderId = claims["providerId"] as String
        val tokenEmail = claims["email"] as String
        val subject = claims.subject

        if (subject != "register") {
            throw InvalidTokenSubject()
        }

        if (tokenProvider != provider.name) {
            throw ProviderMismatch(provider.name, tokenProvider)
        }

        val finalEmail = request.email ?: tokenEmail

        if (!finalEmail.endsWith("@snu.ac.kr")) {
            throw InvalidEmailDomain()
        }

        val newUser = User(
            username = finalEmail,
            password = null,
            major = request.major,
            studentNumber = request.studentNumber,
            nickname = request.nickname,
            isVerified = true,
            profileImageUrl = null,
            userRole = Role.USER,
            bio = null,
        )
        val savedUser = userRepository.save(newUser)

        val newSocialAuth = SocialAuth(
            user = savedUser,
            provider = provider,
            providerId = tokenProviderId,
        )
        socialAuthRepository.save(newSocialAuth)

        val accessToken = jwtTokenProvider.createAccessToken(savedUser.username)

        return OAuthSignUpResponse(
            accessToken = accessToken,
            username = savedUser.username,
            nickname = savedUser.nickname,
            isVerified = savedUser.isVerified
        )
    }
}
