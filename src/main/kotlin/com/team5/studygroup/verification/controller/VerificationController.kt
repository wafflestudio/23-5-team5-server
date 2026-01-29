package com.team5.studygroup.verification.controller

import com.team5.studygroup.common.ErrorResponse
import com.team5.studygroup.oauth.dto.OAuthLoginResponse
import com.team5.studygroup.verification.dto.EmailRequest
import com.team5.studygroup.verification.dto.SocialVerifyRequest
import com.team5.studygroup.verification.dto.VerifyRequest
import com.team5.studygroup.verification.service.VerificationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Verification API", description = "이메일 인증 및 소셜 추가 인증 관련 API")
@RestController
@RequestMapping("/auth")
class VerificationController(
    private val verificationService: VerificationService,
) {
    @Operation(
        summary = "인증번호 발송",
        description =
            "입력한 이메일로 6자리 인증번호를 발송합니다.\n\n" +
                "- **서울대학교 이메일(@snu.ac.kr)**만 사용 가능합니다.\n" +
                "- 인증번호 유효 시간은 **3분**입니다.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "발송 성공",
                content = [Content(schema = Schema(implementation = String::class))],
            ),
            ApiResponse(
                responseCode = "400",
                description = "도메인 오류",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = ErrorResponse::class),
                    examples = [ExampleObject(
                        name = "잘못된 도메인",
                        value = """
                            {
                              "errorCode": 2001,
                              "message": "서울대학교 이메일(@snu.ac.kr)만 사용할 수 있습니다.",
                              "timestamp": "2026-01-30T05:00:00"
                            }
                        """
                    )]
                )]
            ),
            ApiResponse(
                responseCode = "500",
                description = "메일 서버 오류",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = ErrorResponse::class),
                    examples = [ExampleObject(
                        name = "발송 실패",
                        value = """
                            {
                              "errorCode": 2002,
                              "message": "메일 발송에 실패했습니다. 이메일 주소를 확인해주세요.",
                              "timestamp": "2026-01-30T05:00:00"
                            }
                        """
                    )]
                )]
            ),
        ],
    )
    @PostMapping("/code")
    fun sendCode(
        @RequestBody request: EmailRequest,
    ): ResponseEntity<String> {
        verificationService.sendCode(request.email)
        return ResponseEntity.ok("인증번호가 발송되었습니다. (유효시간 3분)")
    }

    @Operation(
        summary = "인증번호 검증 (일반 회원가입)",
        description = "이메일과 인증번호를 입력받아 검증합니다.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "인증 성공",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [ExampleObject(value = "{\"message\": \"인증에 성공하였습니다.\"}")],
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "400",
                description = "검증 실패",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = ErrorResponse::class),
                    examples = [
                        ExampleObject(
                            name = "번호 만료 또는 미발급",
                            value = """
                            {
                              "errorCode": 2003,
                              "message": "인증번호가 만료되었거나 발송되지 않았습니다.",
                              "timestamp": "2026-01-30T05:00:00"
                            }
                        """
                        ),
                        ExampleObject(
                            name = "번호 불일치",
                            value = """
                            {
                              "errorCode": 2004,
                              "message": "인증번호가 일치하지 않습니다.",
                              "timestamp": "2026-01-30T05:00:00"
                            }
                        """
                        )
                    ]
                )]
            ),
        ],
    )
    @PostMapping("/verify")
    fun verify(
        @RequestBody request: VerifyRequest,
    ): ResponseEntity<Map<String, String>> {
        return ResponseEntity.ok(
            mapOf(
                "message" to verificationService.verify(request),
            ),
        )
    }

    @Operation(
        summary = "소셜 로그인 회원가입 추가 인증",
        description =
            "소셜 로그인 시도 후, 'registerToken'과 함께 이메일 인증을 수행합니다.\n\n" +
                "- 회원가입한 계정이 있다면 LOGIN 상태와 accessToken 반환, 메인페이지로 이동합니다.\n" +
                "- 신규 유저라면 REGISTER 상태와 registerToken 반환\n" +
                "- type이 REGISTER이면 닉네임, 학번, 전공을 추가로 입력받아\n" +
                "- /api/oauth/signUp/{provider} api를 호출합니다.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "인증 및 처리 성공 (Type: LOGIN 또는 REGISTER)",
                content = [Content(schema = Schema(implementation = OAuthLoginResponse::class))],
            ),
            ApiResponse(
                responseCode = "400",
                description = "검증 실패",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = ErrorResponse::class),
                    examples = [
                        ExampleObject(
                            name = "번호 만료 또는 미발급",
                            value = """
                            {
                              "errorCode": 2003,
                              "message": "인증번호가 만료되었거나 발송되지 않았습니다.",
                              "timestamp": "2026-01-30T05:00:00"
                            }
                        """
                        ),
                        ExampleObject(
                            name = "번호 불일치",
                            value = """
                            {
                              "errorCode": 2004,
                              "message": "인증번호가 일치하지 않습니다.",
                              "timestamp": "2026-01-30T05:00:00"
                            }
                        """
                        )
                    ]
                )]
            ),
        ],
    )
    @PostMapping("/social/verify")
    fun socialVerify(
        @RequestBody request: SocialVerifyRequest,
    ): ResponseEntity<OAuthLoginResponse> {
        val response = verificationService.verifyAndProcess(request)
        return ResponseEntity.ok(response)
    }
}
