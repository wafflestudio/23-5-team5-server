package com.team5.studygroup.helper

import com.team5.studygroup.jwt.JwtTokenProvider
import com.team5.studygroup.user.model.ProviderType
import com.team5.studygroup.user.model.SocialAuth
import com.team5.studygroup.user.model.User
import com.team5.studygroup.user.repository.SocialAuthRepository
import com.team5.studygroup.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class DataGenerator(
    private val userRepository: UserRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val passwordEncoder: PasswordEncoder,
    private val socialAuthRepository: SocialAuthRepository,
) {
    fun generateUser(
        username: String? = null,
        password: String? = null,
        major: String? = null,
        studentNumber: String? = null,
        nickname: String? = null,
    ): Pair<User, String> {
        val user =
            userRepository.save(
                User(
                    username = username ?: "user-${Random.nextInt(1000000)}",
                    password = passwordEncoder.encode(password ?: "password-${Random.nextInt(1000000)}"),
                    major = major ?: "major-${Random.nextInt(1000000)}",
                    studentNumber = studentNumber ?: "studentNumber-${Random.nextInt(1000000)}",
                    nickname = nickname ?: "nickname-${Random.nextInt(1000000)}",
                ),
            )
        return user to jwtTokenProvider.createAccessToken(user.username)
    }

    fun generateSocialAuth(
        provider: ProviderType,
        providerId: String? = null,
    ): Pair<User, String> {
        val (user, _) = generateUser()
        val socialAuth =
            socialAuthRepository.save(
                SocialAuth(
                    user = user,
                    provider = provider,
                    providerId = providerId ?: "providerId-${Random.nextInt(1000000)}",
                ),
            )
        return user to socialAuth.providerId
    }
}
