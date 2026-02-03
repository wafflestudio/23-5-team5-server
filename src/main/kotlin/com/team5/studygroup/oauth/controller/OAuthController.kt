package com.team5.studygroup.oauth.controller

import com.team5.studygroup.common.ErrorResponse
import com.team5.studygroup.oauth.dto.OAuthLoginResponse
import com.team5.studygroup.oauth.dto.OAuthSignUpRequest
import com.team5.studygroup.oauth.dto.OAuthSignUpResponse
import com.team5.studygroup.oauth.dto.SocialLoginRequest
import com.team5.studygroup.oauth.service.OAuthService
import com.team5.studygroup.user.model.ProviderType
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "소셜 로그인 인증 API", description = "Google, Kakao 등 소셜 로그인 및 회원가입 관련 API")
@RestController
@RequestMapping("/oauth")
class OAuthController(
    private val oauthService: OAuthService,
) {
    @Operation(
        summary = "소셜 로그인 요청",
        description = """
            소셜 플랫폼의 액세스 토큰을 받아 로그인을 시도합니다.
            - **기존 회원**: `LOGIN` 상태와 `accessToken` 반환, 바로 메인페이지로 이동합니다.
            - **신규 회원**: `REGISTER` 상태와 `registerToken` 반환 (이 토큰으로 회원가입 진행)
            - registerToken을 받은 경우 파싱해서 안에 들어있는 메일이 스누메일일 경우 재학생 인증을
            - 건너뛰고 추가 정보만을 입력 받아 /oauth/signup/{provider} api를 호출합니다.
            - 스누메일이 아닐 경우 /auth/social/verify api를 호출하여 재학생 인증을 합니다.
        """,
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "로그인 성공 또는 가입 절차 필요",
                content = [Content(schema = Schema(implementation = OAuthLoginResponse::class))],
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "지원하지 않는 소셜 타입",
                                summary = "지원하지 않는 Provider 입력 시",
                                value = """
                                    {"errorCode": 400,
                                     "message": "지원하지 않는 소셜 타입입니다: NAVER", 
                                     "timestamp": "2026-01-30T05:00:00"}
                                     """,
                            ),
                        ],
                    ),
                ],
            ),
        ],
    )
    @PostMapping("/login/{provider}")
    fun login(
        @RequestBody request: SocialLoginRequest,
        @Parameter(description = "소셜 공급자 타입 (GOOGLE, KAKAO)", example = "GOOGLE", required = true)
        @PathVariable provider: String,
    ): ResponseEntity<OAuthLoginResponse> {
        val providerType =
            runCatching { ProviderType.valueOf(provider.uppercase()) }
                .getOrElse { throw IllegalArgumentException("지원하지 않는 소셜 타입입니다: $provider") }

        val response = oauthService.login(providerType, request.token)
        return ResponseEntity.ok(response)
    }

    @Operation(
        summary = "소셜 회원가입 (추가 정보 입력)",
        description = """
            - registerToken과 추가 정보(학번, 학과 등)를 받아 회원가입을 완료합니다.
            - 만약 registerToken 안에 들어있는 email이 스누메일이면 request의 email에 
            - null을 넣어서 보내고 스누메일이 아니면 재학생 인증에서 입력받은 스누메일을 넣습니다.
        """,
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "회원가입 성공 및 토큰 발급",
                content = [Content(schema = Schema(implementation = OAuthSignUpResponse::class))],
            ),
            ApiResponse(
                responseCode = "400",
                description = "토큰 또는 요청 정보 부적합",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "토큰 용도 불일치",
                                summary = "회원가입용 토큰이 아닌 경우",
                                value = """
                                    {"errorCode": 4002, 
                                    "message": "회원가입 전용 토큰이 아닙니다.", 
                                    "timestamp": "2026-01-30T05:00:00"}
                                    """,
                            ),
                            ExampleObject(
                                name = "Provider 불일치",
                                summary = "토큰의 공급자와 요청 경로의 공급자가 다를 때",
                                value = """
                                    {"errorCode": 4003, 
                                    "message": "잘못된 접근입니다. (Provider 불일치: 요청=GOOGLE, 토큰=KAKAO)", 
                                    "timestamp": "2026-01-30T05:00:00"}
                                    """,
                            ),
                        ],
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "401",
                description = "인증 실패",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "가입 토큰 만료",
                                summary = "유효하지 않거나 만료된 registerToken",
                                value = """
                                    {"errorCode": 4001, 
                                    "message": "유효하지 않거나 만료된 가입 토큰입니다.", 
                                    "timestamp": "2026-01-30T05:00:00"}
                                    """,
                            ),
                        ],
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "403",
                description = "권한 없음",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "도메인 제한",
                                summary = "서울대 이메일이 아닌 경우",
                                value = """
                                    {"errorCode": 4004,
                                     "message": "서울대 이메일(@snu.ac.kr)만 가입 가능합니다.",
                                     "timestamp": "2026-01-30T05:00:00"}
                                     """,
                            ),
                        ],
                    ),
                ],
            ),
        ],
    )
    @PostMapping("/signUp/{provider}")
    fun signUp(
        @RequestBody request: OAuthSignUpRequest,
        @Parameter(description = "소셜 공급자 타입 (GOOGLE, KAKAO)", example = "GOOGLE", required = true)
        @PathVariable provider: String,
    ): ResponseEntity<OAuthSignUpResponse> {
        val providerType =
            runCatching { ProviderType.valueOf(provider.uppercase()) }
                .getOrElse { throw IllegalArgumentException("지원하지 않는 소셜 타입입니다: $provider") }

        val response = oauthService.signUp(providerType, request)
        return ResponseEntity.ok(response)
    }
}
