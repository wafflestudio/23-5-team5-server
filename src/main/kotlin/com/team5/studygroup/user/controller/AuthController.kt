package com.team5.studygroup.user.controller

import com.team5.studygroup.common.ErrorResponse
import com.team5.studygroup.user.dto.LoginDto
import com.team5.studygroup.user.dto.LoginResponseDto
import com.team5.studygroup.user.dto.SignUpDto
import com.team5.studygroup.user.dto.SignUpResponseDto
import com.team5.studygroup.user.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.MediaType
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
    @Operation(
        summary = "일반 회원가입",
        description =
            "아이디(스누메일), 비밀번호, 학번 등을 입력받아 회원가입을 진행합니다. " +
                "(소셜 로그인 사용자의 일반 회원 전환 포함)",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "회원가입 성공",
                content = [Content(schema = Schema(implementation = SignUpResponseDto::class))],
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 (입력값 검증 실패)",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "검증 오류",
                                summary = "입력값 유효성 검사 실패 예시",
                                value = """
                                    {"errorCode": 9001, 
                                    "message": "@snu.ac.kr로 끝나는 학교 이메일이어야 합니다.", 
                                    "timestamp": "2025-05-21T10:00:00"}
                                    """,
                            ),
                        ],
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "409",
                description = "이미 사용 중인 닉네임",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "닉네임 중복",
                                summary = "이미 존재하는 닉네임 요청 시",
                                value = """
                                    {"errorCode": 1002, 
                                    "message": "이미 사용 중인 닉네임입니다.", 
                                    "timestamp": "2024-05-21T10:00:00"}""",
                            ),
                            ExampleObject(
                                name = "아이디 중복",
                                summary = "이미 가입된 이메일",
                                value = """
                                    {"errorCode": 1004, 
                                    "message": "이미 등록된 아이디입니다.", 
                                    "timestamp": "2026-01-30T05:00:00"}""",
                            ),
                            ExampleObject(
                                name = "학번 중복",
                                summary = "이미 가입된 학번",
                                value = """
                                    {"errorCode": 1005, 
                                    "message": "이미 등록된 학번입니다.", 
                                    "timestamp": "2026-01-30T05:00:00"}""",
                            ),
                        ],
                    ),
                ],
            ),
        ],
    )
    @PostMapping("/signup")
    fun signUp(
        @Valid @RequestBody signUpDto: SignUpDto,
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
                description = "아이디 또는 비밀번호 오류",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "아이디 또는 비밀번호 오류",
                                summary = "잘못된 아이디 또는 비밀번호로 로그인 요청 시",
                                value = """
                                    {"errorCode": 1005,
                                     "message": "아이디 또는 비밀번호가 틀립니다.",
                                      "timestamp": "2024-05-21T10:00:00"
                                      }
                                    """,
                            ),
                        ],
                    ),
                ],
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
