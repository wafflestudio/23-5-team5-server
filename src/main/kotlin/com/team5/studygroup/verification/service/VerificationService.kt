package com.team5.studygroup.verification.service

import com.team5.studygroup.common.RedisService
import com.team5.studygroup.jwt.JwtTokenProvider
import com.team5.studygroup.oauth.dto.OAuthLoginResponse
import com.team5.studygroup.user.model.ProviderType
import com.team5.studygroup.user.model.SocialAuth
import com.team5.studygroup.user.repository.SocialAuthRepository
import com.team5.studygroup.user.repository.UserRepository
import com.team5.studygroup.verification.dto.VerifyRequest
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Random

@Service
class VerificationService(
    private val redisService: RedisService,
    private val javaMailSender: JavaMailSender,
    private val userRepository: UserRepository,
    private val socialAuthRepository: SocialAuthRepository,
    private val jwtTokenProvider: JwtTokenProvider,
) {
    fun sendCode(email: String) {
        if (!email.endsWith("snu.ac.kr")) {
            throw IllegalArgumentException("서울대학교 이메일(@snu.ac.kr)만 사용할 수 있습니다.")
        }

        val code = createRandomCode()

        redisService.setDataExpire(email, code, 180)

        try {
            val message = SimpleMailMessage()
            message.setTo(email)
            message.setSubject("[StudyGroup] 재학생 인증번호입니다.")
            message.setText("인증번호는 [$code] 입니다.\n3분 안에 입력해주세요.")
            javaMailSender.send(message)
        } catch (e: Exception) {
            redisService.deleteData(email)
            throw RuntimeException("메일 발송에 실패했습니다. 이메일 주소를 확인해주세요.")
        }
    }

    @Transactional
    fun verifyAndProcess(request: VerifyRequest): OAuthLoginResponse {
        val (registerToken, inputEmail, code) = request

        val storedCode = redisService.getData(inputEmail)
        if (storedCode == null) {
            throw IllegalArgumentException("인증번호가 만료되었거나 발송되지 않았습니다.")
        }
        if (storedCode != code) {
            throw IllegalArgumentException("인증번호가 일치하지 않습니다.")
        }

        val claims = jwtTokenProvider.getRegisterClaims(registerToken)
        val providerStr = claims["provider"] as String
        val providerId = claims["providerId"] as String
        val socialProvider = ProviderType.valueOf(providerStr)

        redisService.deleteData(inputEmail)

        val existingUser = userRepository.findByUsername(inputEmail)

        if (existingUser != null) {
            if (!socialAuthRepository.existsByProviderAndProviderId(socialProvider, providerId)) {
                val newSocialAuth =
                    SocialAuth(
                        user = existingUser,
                        provider = socialProvider,
                        providerId = providerId,
                    )
                socialAuthRepository.save(newSocialAuth)
            }

            return OAuthLoginResponse(
                type = "LOGIN",
                token = jwtTokenProvider.createAccessToken(existingUser.username),
            )
        } else {
            val verifiedToken =
                jwtTokenProvider.createRegisterToken(
                    provider = providerStr,
                    providerId = providerId,
                    email = inputEmail,
                )

            return OAuthLoginResponse(
                type = "REGISTER",
                token = verifiedToken,
            )
        }
    }

    private fun createRandomCode(): String {
        return Random().nextInt(1000000).toString().padStart(6, '0')
    }
}
