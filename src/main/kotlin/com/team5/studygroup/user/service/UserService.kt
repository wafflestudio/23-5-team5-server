package com.team5.studygroup.user.service

import com.team5.studygroup.common.RedisService
import com.team5.studygroup.jwt.JwtTokenProvider
import com.team5.studygroup.user.AlreadyLoggedOutException
import com.team5.studygroup.user.InvalidTokenFormatException
import com.team5.studygroup.user.LoginFailedException
import com.team5.studygroup.user.NicknameDuplicateException
import com.team5.studygroup.user.StudentNumberDuplicateException
import com.team5.studygroup.user.UsernameDuplicateException
import com.team5.studygroup.user.dto.LoginDto
import com.team5.studygroup.user.dto.LoginResponseDto
import com.team5.studygroup.user.dto.SignUpDto
import com.team5.studygroup.user.dto.SignUpResponseDto
import com.team5.studygroup.user.model.Role
import com.team5.studygroup.user.model.User
import com.team5.studygroup.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val redisService: RedisService,
) {
    @Transactional
    fun signUp(signUpDto: SignUpDto): SignUpResponseDto {
        val existingUser = userRepository.findByUsername(signUpDto.username)

        val userToSave =
            if (existingUser != null) {
                if (existingUser.password == null) {
                    if (existingUser.nickname != signUpDto.nickname && userRepository.existsByNickname(signUpDto.nickname)) {
                        throw NicknameDuplicateException()
                    }
                    if (existingUser.studentNumber != signUpDto.studentNumber && userRepository.existsByStudentNumber(signUpDto.studentNumber)) {
                        throw StudentNumberDuplicateException()
                    }

                    existingUser.apply {
                        this.password = passwordEncoder.encode(signUpDto.password)
                        this.major = signUpDto.major
                        this.studentNumber = signUpDto.studentNumber
                        this.nickname = signUpDto.nickname
                    }
                } else {
                    throw UsernameDuplicateException()
                }
            } else {
                if (userRepository.existsByNickname(signUpDto.nickname)) {
                    throw NicknameDuplicateException()
                }
                if (userRepository.existsByStudentNumber(signUpDto.studentNumber)) {
                    throw StudentNumberDuplicateException()
                }

                User(
                    username = signUpDto.username,
                    password = passwordEncoder.encode(signUpDto.password),
                    major = signUpDto.major,
                    studentNumber = signUpDto.studentNumber,
                    nickname = signUpDto.nickname,
                    isVerified = true,
                    profileImageUrl = null,
                    userRole = Role.USER,
                    bio = null,
                )
            }

        val savedUser = userRepository.save(userToSave)

        val accessToken = jwtTokenProvider.createAccessToken(savedUser.username)

        return SignUpResponseDto(
            accessToken = accessToken,
            username = savedUser.username,
            nickname = savedUser.nickname,
            isVerified = savedUser.isVerified,
        )
    }

    fun login(loginDto: LoginDto): LoginResponseDto {
        val member =
            userRepository.findByUsername(loginDto.username)
                ?: throw LoginFailedException()

        if (member.password == null || !passwordEncoder.matches(loginDto.password, member.password)) {
            throw LoginFailedException()
        }

        val accessToken = jwtTokenProvider.createAccessToken(member.username)

        return LoginResponseDto(
            accessToken = accessToken,
            nickname = member.nickname,
            isVerified = member.isVerified,
        )
    }

    @Transactional
    fun logout(authHeader: String) {
        // 헤더 검증: Bearer 토큰인지 확인
        if (!authHeader.startsWith("Bearer ")) {
            throw InvalidTokenFormatException()
        }

        // "Bearer " 제거 후 토큰 값만 추출
        val accessToken = authHeader.substring(7)

        // 이미 블랙리스트(로그아웃) 처리된 토큰인지 확인
        if (redisService.isBlacklisted(accessToken)) {
            throw AlreadyLoggedOutException()
        }

        // 4. 블랙리스트 등록
        // 토큰의 남은 유효 시간(밀리초)을 계산하여 그 시간만큼만 Redis에 저장
        val expiration: Long = jwtTokenProvider.getExpiration(accessToken)
        val now: Long = System.currentTimeMillis()
        val remainingTime = expiration - now

        if (remainingTime > 0) {
            redisService.setBlackList(accessToken, "logout", remainingTime)
        }
    }
}
