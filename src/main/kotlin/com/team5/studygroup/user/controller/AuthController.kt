package com.team5.studygroup.user.controller

import com.team5.studygroup.user.dto.LoginDto
import com.team5.studygroup.user.dto.LoginResponseDto
import com.team5.studygroup.user.dto.SignUpDto
import com.team5.studygroup.user.dto.SignUpResponseDto
import com.team5.studygroup.user.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val userService: UserService,
) {
    @PostMapping("/signup")
    fun signUp(
        @RequestBody signUpDto: SignUpDto,
    ): ResponseEntity<SignUpResponseDto> { // String 대신 DTO 반환
        val response = userService.signUp(signUpDto)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/login")
    fun login(
        @RequestBody loginDto: LoginDto,
    ): ResponseEntity<LoginResponseDto> { // LoginResponseDto로 타입 일치
        val response = userService.login(loginDto)
        return ResponseEntity.ok(response)
    }
}
