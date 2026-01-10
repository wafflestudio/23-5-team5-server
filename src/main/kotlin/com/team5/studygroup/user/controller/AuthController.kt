package com.team5.studygroup.user.controller

import com.team5.studygroup.user.dto.LoginDto
import com.team5.studygroup.user.dto.SignUpDto
import com.team5.studygroup.user.dto.TokenInfo
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class AuthController(
    private val memberService: com.team5.studygroup.user.service.UserService,
) {
    @PostMapping("/signup")
    fun signUp(
        @RequestBody signUpDto: SignUpDto,
    ): String = memberService.signUp(signUpDto)

    @PostMapping("/login")
    fun login(
        @RequestBody loginDto: LoginDto,
    ): TokenInfo = memberService.login(loginDto)
}
