package com.team5.studygroup.verification.service

import com.team5.studygroup.common.RedisService
import com.team5.studygroup.jwt.JwtTokenProvider
import com.team5.studygroup.oauth.dto.OAuthLoginResponse
import com.team5.studygroup.user.model.ProviderType
import com.team5.studygroup.user.model.SocialAuth
import com.team5.studygroup.user.repository.SocialAuthRepository
import com.team5.studygroup.user.repository.UserRepository
import com.team5.studygroup.verification.EmailSendFailedException
import com.team5.studygroup.verification.InvalidEmailDomainException
import com.team5.studygroup.verification.VerificationCodeExpiredException
import com.team5.studygroup.verification.VerificationCodeMismatchException
import com.team5.studygroup.verification.dto.SocialVerifyRequest
import com.team5.studygroup.verification.dto.VerifyRequest
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.scheduling.annotation.Async
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
    @Async
    fun sendCode(email: String) {
        if (!email.endsWith("snu.ac.kr")) {
            throw InvalidEmailDomainException()
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
            throw EmailSendFailedException()
        }
    }

    @Transactional
    fun verify(request: VerifyRequest): String {
        val storedCode = redisService.getData(request.email)
        if (storedCode == null) {
            throw VerificationCodeExpiredException()
        }
        if (storedCode != request.code) {
            throw VerificationCodeMismatchException()
        }
        redisService.deleteData(request.email)
        return "인증에 성공하였습니다."
    }

    @Transactional
    fun verifyAndProcess(request: SocialVerifyRequest): OAuthLoginResponse {
        val (registerToken, inputEmail, code) = request

        val storedCode = redisService.getData(inputEmail)
        if (storedCode == null) {
            throw VerificationCodeExpiredException()
        }
        if (storedCode != code) {
            throw VerificationCodeMismatchException()
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
