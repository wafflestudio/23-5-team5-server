package com.team5.studygroup.verification.controller

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
        description = "입력한 이메일로 6자리 인증번호를 발송합니다.\n\n" +
                "- **서울대학교 이메일(@snu.ac.kr)**만 사용 가능합니다.\n" +
                "- 인증번호 유효 시간은 **3분**입니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "발송 성공",
                content = [Content(schema = Schema(implementation = String::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 이메일 도메인 (@snu.ac.kr 아님)",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "500",
                description = "메일 발송 서버 오류",
                content = [Content(schema = Schema(hidden = true))]
            )
        ]
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
        description = "이메일과 인증번호를 입력받아 검증합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "인증 성공",
                content = [Content(
                    mediaType = "application/json",
                    examples = [ExampleObject(value = "{\"message\": \"인증에 성공하였습니다.\"}")]
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "인증 실패 (인증번호 만료, 미발송, 또는 불일치)",
                content = [Content(schema = Schema(hidden = true))]
            )
        ]
    )
    @PostMapping("/verify")
    fun verify(
        @RequestBody request: VerifyRequest,
    ): ResponseEntity<Map<String, String>> {
        return ResponseEntity.ok(
            mapOf(
                "message" to verificationService.verify(request)
            )
        )
    }

    @Operation(
        summary = "소셜 로그인 회원가입 추가 인증",
        description = "소셜 로그인 시도 후, 'Register Token'과 함께 이메일 인증을 수행합니다.\n\n" +
                "- 회원가입한 계정이 있다면 LOGIN 상태와 accessToken 반환, 메인페이지로 이동합니다.\n" +
                "- 신규 유저라면 REGISTER 상태와 registerToken 반환\n" +
                "- type이 REGISTER이면 닉네임, 학번, 전공을 추가로 입력받아\n" +
                "- /api/oauth/signUp/{provider} api를 호출합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "인증 및 처리 성공 (Type: LOGIN 또는 REGISTER)",
                content = [Content(schema = Schema(implementation = OAuthLoginResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "인증 실패 (인증번호 만료, 불일치 등)",
                content = [Content(schema = Schema(hidden = true))]
            )
        ]
    )
    @PostMapping("/social/verify")
    fun socialVerify(
        @RequestBody request: SocialVerifyRequest,
    ): ResponseEntity<OAuthLoginResponse> {
        val response = verificationService.verifyAndProcess(request)
        return ResponseEntity.ok(response)
    }
}