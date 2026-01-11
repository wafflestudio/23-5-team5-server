package com.team5.studygroup.user.service

import com.team5.studygroup.user.dto.LoginDto
import com.team5.studygroup.user.dto.SignUpDto
import com.team5.studygroup.user.model.User
import com.team5.studygroup.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
//    private val authenticationManagerBuilder: AuthenticationManagerBuilder,
//    private val jwtTokenProvider: JwtTokenProvider,
) {
    @Transactional
    fun signUp(signUpDto: SignUpDto): String {
        // 1. 중복 체크 (아이디뿐만 아니라 이메일, 닉네임도 체크하는 것이 안전합니다)
        if (userRepository.findByUsername(signUpDto.username) != null) {
            throw Exception("이미 등록된 아이디입니다.")
        }
        if (userRepository.findByEmail(signUpDto.email) != null) {
            throw Exception("이미 등록된 이메일입니다.")
        }

        // 2. 유저 객체 생성
        // DTO에 major, studentNumber 등이 포함되어 있다고 가정하고 매핑합니다.
        val member =
            User(
                username = signUpDto.username,
                password = signUpDto.password,
                // TODO: BCryptPasswordEncoder로 암호화 필요!
                email = signUpDto.email,
                major = signUpDto.major,
                studentNumber = signUpDto.studentNumber,
                nickname = signUpDto.nickname,
                // 닉네임 없으면 아이디로
                isVerified = false,
                profileImageUrl = null,
                bio = null,
            )

        userRepository.save(member)
        return "${member.username}님, 회원가입이 완료되었습니다."
    }

    fun login(loginDto: LoginDto): String {
        // "일단 JWT 안 쓸 거야"라고 하셨으므로,
        // 만약 JWT 설정이 미비하다면 이 부분에서 에러가 날 수 있습니다.
        // API 설정만 확인하시려면 이 메서드는 잠시 비워두거나 더미 토큰을 반환하게 해도 됩니다.
        return "JWT 설정 전입니다."

//        val authenticationToken = UsernamePasswordAuthenticationToken(loginDto.account, loginDto.password)
//        val authentication = authenticationManagerBuilder.`object`.authenticate(authenticationToken)
//
//        return jwtTokenProvider.createToken(authentication)
    }
}
