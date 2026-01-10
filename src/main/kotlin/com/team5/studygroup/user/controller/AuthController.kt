/*
package com.team5.studygroup.user.controller

import com.team5.studygroup.dto.LoginDto
import com.team5.studygroup.dto.SignUpDto
import com.team5.studygroup.dto.TokenInfo
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class AuthController(
    private val memberService: com.team5.studygroup.user.service.UserService,
) {
    @PostMapping("/signup")
    fun signUp(@RequestBody signUpDto: SignUpDto): String =
        memberService.signUp(signUpDto)

    @PostMapping("/login")
    fun login(@RequestBody loginDto: LoginDto): TokenInfo =
        memberService.login(loginDto)
}*/
