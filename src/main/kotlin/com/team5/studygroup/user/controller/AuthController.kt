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
@RequestMapping("/auth")
class AuthController(
    private val userService: UserService,
) {
    @PostMapping("/signup")
    fun signUp(
        @RequestBody signUpDto: SignUpDto,
    ): ResponseEntity<String> {
        val message = userService.signUp(signUpDto)
        return ResponseEntity.ok(message)
    }

    @PostMapping("/login")
    fun login(
        @RequestBody loginDto: LoginDto,
    ): ResponseEntity<String> {
        val token = userService.login(loginDto)
        return ResponseEntity.ok(token)
    }
}
