package com.team5.studygroup.user.service

import com.team5.studygroup.dto.LoginDto
import com.team5.studygroup.dto.SignUpDto
import com.team5.studygroup.dto.TokenInfo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: com.team5.studygroup.user.repository.UserRepository,
    private val authenticationManagerBuilder: org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder,
    private val jwtTokenProvider: com.team5.studygroup.jwt.JwtTokenProvider,
) {
    @org.springframework.transaction.annotation.Transactional
    fun signUp(signUpDto: SignUpDto): kotlin.String {
        var member = userRepository.findByUsername(signUpDto.username)
        if (member != null) {
            throw _root_ide_package_.kotlin.Exception("이미 등록된 아이디입니다.")
        }

        member = _root_ide_package_.com.team5.studygroup.user.model.User(
            username = signUpDto.username,
            password = signUpDto.password,
            email = signUpDto.email
        )

        userRepository.save(member)
        return "회원가입이 완료되었습니다."
    }

    fun login(loginDto: LoginDto): TokenInfo {
        val authenticationToken =
            _root_ide_package_.org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                loginDto.account,
                loginDto.password
            )
        val authentication = authenticationManagerBuilder.`object`.authenticate(authenticationToken)
        return jwtTokenProvider.createToken(authentication)
    }
}