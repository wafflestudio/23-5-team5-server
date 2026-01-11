package com.team5.studygroup.user.service

import com.team5.studygroup.jwt.JwtTokenProvider
import com.team5.studygroup.user.dto.LoginDto
import com.team5.studygroup.user.dto.SignUpDto
import com.team5.studygroup.user.dto.TokenInfo
import com.team5.studygroup.user.model.User
import com.team5.studygroup.user.repository.UserRepository
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val authenticationManagerBuilder: AuthenticationManagerBuilder,
    private val jwtTokenProvider: JwtTokenProvider,
) {
    @Transactional // 쓰기 작업이므로 readOnly = false (기본값) 적용
    fun signUp(signUpDto: SignUpDto): String {
        // 1. 아이디 중복 체크
        if (userRepository.findByUsername(signUpDto.username) != null) {
            throw Exception("이미 등록된 아이디입니다.")
        }

        // 2. 유저 객체 생성 (비밀번호 암호화는 나중에 꼭 추가해야 합니다!)
        val member =
            User(
                username = signUpDto.username,
                password = signUpDto.password,
                email = signUpDto.email,
                major = "",
                studentNumber = "",
                nickname = "",
                isVerified = false,
                profileImageUrl = "",
                bio = "",
            )

        userRepository.save(member)
        return "회원가입이 완료되었습니다."
    }

    fun login(loginDto: LoginDto): TokenInfo {
        // principal로 loginDto.account(아마 username이나 email)를 사용
        val authenticationToken = UsernamePasswordAuthenticationToken(loginDto.account, loginDto.password)

        // 실제 인증 수행
        val authentication = authenticationManagerBuilder.`object`.authenticate(authenticationToken)

        // JWT 토큰 생성 및 반환
        return jwtTokenProvider.createToken(authentication)
    }
}
