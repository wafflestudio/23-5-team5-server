package com.team5.studygroup.oauth.controller

import com.team5.studygroup.oauth.dto.OAuthLoginResponse
import com.team5.studygroup.oauth.dto.OAuthSignUpRequest
import com.team5.studygroup.oauth.dto.OAuthSignUpResponse
import com.team5.studygroup.oauth.dto.SocialLoginRequest
import com.team5.studygroup.oauth.service.OAuthService
import com.team5.studygroup.user.model.ProviderType
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "요청 성공 (로그인 완료 또는 회원가입 필요)",
                content = [Content(schema = Schema(implementation = OAuthLoginResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 소셜 공급자(Provider) 타입 (예: GOOGLE, KAKAO 외 값 입력)",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "500",
                description = "소셜 서버 통신 에러 또는 내부 서버 에러",
                content = [Content(schema = Schema(hidden = true))]
            )
        ]
    )
    @PostMapping("/login/{provider}")
    fun login(
        @RequestBody request: SocialLoginRequest,
        @Parameter(description = "소셜 공급자 타입 (GOOGLE, KAKAO)", example = "GOOGLE", required = true)
        @PathVariable provider: String,
    ): ResponseEntity<OAuthLoginResponse> {
        val providerType = runCatching { ProviderType.valueOf(provider.uppercase()) }
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
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "회원가입 성공 및 액세스 토큰 발급",
                content = [Content(schema = Schema(implementation = OAuthSignUpResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = """
                    1. 잘못된 토큰 용도 (회원가입용 토큰이 아님)
                    2. Provider 불일치 (요청한 Provider와 토큰의 Provider가 다름)
                """,
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "유효하지 않거나 만료된 가입 토큰 (재로그인 필요)",
                content = [Content(schema = Schema(hidden = true))]
            ),
            ApiResponse(
                responseCode = "403",
                description = "가입 불가능한 이메일 도메인 (서울대 메일 아님)",
                content = [Content(schema = Schema(hidden = true))]
            )
        ]
    )
    @PostMapping("/signUp/{provider}")
    fun signUp(
        @RequestBody request: OAuthSignUpRequest,
        @Parameter(description = "소셜 공급자 타입 (GOOGLE, KAKAO)", example = "GOOGLE", required = true)
        @PathVariable provider: String,
    ): ResponseEntity<OAuthSignUpResponse> {
        val providerType = runCatching { ProviderType.valueOf(provider.uppercase()) }
            .getOrElse { throw IllegalArgumentException("지원하지 않는 소셜 타입입니다: $provider") }

        val response = oauthService.signUp(providerType, request)
        return ResponseEntity.ok(response)
    }
}