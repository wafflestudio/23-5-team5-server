package com.team5.studygroup.user.service

import com.team5.studygroup.jwt.JwtTokenProvider
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
//    private val authenticationManagerBuilder: AuthenticationManagerBuilder,
    private val jwtTokenProvider: JwtTokenProvider,
) {
    @Transactional
    fun signUp(signUpDto: SignUpDto): SignUpResponseDto {
        // 1. 중복 체크 (아이디뿐만 아니라 닉네임도 체크하는 것이 안전합니다)
        if (userRepository.findByUsername(signUpDto.username) != null) {
            throw Exception("이미 등록된 아이디입니다.")
        }
        if (userRepository.existsByNickname(signUpDto.nickname)) {
            throw Exception("이미 등록된 닉네임입니다.")
        }
        if (userRepository.existsByStudentNumber(signUpDto.studentNumber)) {
            throw Exception("이미 등록된 학번입니다.")
        }

        // 2. 유저 객체 생성
        // DTO에 major, studentNumber 등이 포함되어 있다고 가정하고 매핑합니다.
        val member =
            User(
                username = signUpDto.username,
                password = passwordEncoder.encode(signUpDto.password),
                // TODO: BCryptPasswordEncoder로 암호화 필요!
                major = signUpDto.major,
                studentNumber = signUpDto.studentNumber,
                nickname = signUpDto.nickname,
                // 닉네임 없으면 아이디로
                isVerified = true,
                profileImageUrl = null,
                userRole = Role.USER,
                bio = null,
            )
        userRepository.save(member)
        val accessToken = jwtTokenProvider.createToken(member.username)
        return SignUpResponseDto(
            accessToken = accessToken,
            username = member.username,
            nickname = member.nickname,
            isVerified = member.isVerified,
        )
    }

    fun login(loginDto: LoginDto): LoginResponseDto {
        // "일단 JWT 안 쓸 거야"라고 하셨으므로,
        // 만약 JWT 설정이 미비하다면 이 부분에서 에러가 날 수 있습니다.
        // API 설정만 확인하시려면 이 메서드는 잠시 비워두거나 더미 토큰을 반환하게 해도 됩니다.
        val member =
            userRepository.findByUsername(loginDto.username)
                ?: throw IllegalArgumentException("아이디 또는 비밀번호가 잘못되었습니다.")

        if (!passwordEncoder.matches(loginDto.password, member.password)) {
            throw IllegalArgumentException("아이디 또는 비밀번호가 잘못되었습니다.")
        }

        val accessToken = jwtTokenProvider.createToken(member.username)

        return LoginResponseDto(
            accessToken = accessToken,
            nickname = member.nickname,
            isVerified = member.isVerified,
        )

//        val authenticationToken = UsernamePasswordAuthenticationToken(loginDto.account, loginDto.password)
//        val authentication = authenticationManagerBuilder.`object`.authenticate(authenticationToken)
//
//        return jwtTokenProvider.createToken(authentication)
    }
}
