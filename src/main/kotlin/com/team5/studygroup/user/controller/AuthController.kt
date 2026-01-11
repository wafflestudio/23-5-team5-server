package com.team5.studygroup.user.controller

import com.team5.studygroup.user.dto.LoginDto
import com.team5.studygroup.user.dto.SignUpDto
import com.team5.studygroup.user.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class AuthController(
    private val memberService: UserService,
    private val userService: UserService,
) {
    @PostMapping("/signup")
    fun signUp(
        @RequestBody signUpDto: SignUpDto,
    ): String = memberService.signUp(signUpDto)

    @PostMapping("/login")
    fun login(
        @RequestBody loginDto: LoginDto,
    ): ResponseEntity<String> {
        val result = userService.login(loginDto)
        return ResponseEntity.ok(result) // 이제 String을 반환하므로 에러가 사라집니다.
    }
}
