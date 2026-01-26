package com.team5.studygroup.user.service

import com.team5.studygroup.jwt.JwtTokenProvider
import com.team5.studygroup.user.LoginFailedException
import com.team5.studygroup.user.NicknameDuplicateException
import com.team5.studygroup.user.StudentNumberDuplicateException
import com.team5.studygroup.user.UserException
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
) {
    @Transactional
    fun signUp(signUpDto: SignUpDto): SignUpResponseDto {

        val existingUser = userRepository.findByUsername(signUpDto.username)

        val userToSave = if (existingUser != null) {

            if (existingUser.password == null) {

                if (userRepository.existsByNickname(signUpDto.nickname)) {
                    throw NicknameDuplicateException()
                }
                if (userRepository.existsByStudentNumber(signUpDto.studentNumber)) {
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
}
