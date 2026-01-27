package com.team5.studygroup.user.controller

import com.team5.studygroup.user.dto.LoginDto
import com.team5.studygroup.user.dto.LoginResponseDto
import com.team5.studygroup.user.dto.SignUpDto
import com.team5.studygroup.user.dto.SignUpResponseDto
import com.team5.studygroup.user.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Auth API", description = "회원가입 및 로그인 관련 API")
@RestController
@RequestMapping("/auth")
class AuthController(
    private val userService: UserService,
) {
    @Operation(summary = "일반 회원가입", description = "아이디(스누메일), 비밀번호, 학번 등을 입력받아 회원가입을 진행합니다. (소셜 로그인 사용자의 일반 회원 전환 포함)")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "회원가입 성공",
                content = [Content(schema = Schema(implementation = SignUpResponseDto::class))],
            ),
            ApiResponse(
                responseCode = "409",
                description = "회원가입 실패 (닉네임 중복, 학번 중복, 또는 이미 존재하는 아이디)",
                content = [Content(schema = Schema(hidden = true))],
            ),
        ],
    )
    @PostMapping("/signup")
    fun signUp(
        @RequestBody signUpDto: SignUpDto,
    ): ResponseEntity<SignUpResponseDto> {
        val response = userService.signUp(signUpDto)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "일반 로그인", description = "아이디와 비밀번호를 이용하여 로그인하고 JWT 토큰을 발급받습니다.")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "로그인 성공",
                content = [Content(schema = Schema(implementation = LoginResponseDto::class))],
            ),
            ApiResponse(
                responseCode = "401",
                description = "로그인 실패 (아이디 또는 비밀번호 불일치)",
                content = [Content(schema = Schema(hidden = true))],
            ),
        ],
    )
    @PostMapping("/login")
    fun login(
        @RequestBody loginDto: LoginDto,
    ): ResponseEntity<LoginResponseDto> {
        val response = userService.login(loginDto)
        return ResponseEntity.ok(response)
    }
}
