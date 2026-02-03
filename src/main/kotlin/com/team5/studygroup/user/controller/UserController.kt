package com.team5.studygroup.user.controller

import com.team5.studygroup.common.ErrorResponse
import com.team5.studygroup.user.LoggedInUser
import com.team5.studygroup.user.dto.GetProfileDto
import com.team5.studygroup.user.dto.UpdateProfileDto
import com.team5.studygroup.user.dto.UpdateProfileImageResponseDto
import com.team5.studygroup.user.service.ProfileService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.net.URI

@Tag(name = "User API", description = "마이페이지 및 프로필 관리 API")
@RestController
@RequestMapping("/users/me")
class UserController(
    private val profileService: ProfileService,
) {
    @Operation(
        summary = "내 프로필 수정",
        description =
            "닉네임, 전공, 자기소개, 프로필 이미지를 수정합니다.\n\n" +
                "- **Multipart/form-data** 형식으로 요청해야 합니다.\n" +
                "- 변경하지 않을 필드는 null로 보내면 기존 값이 유지됩니다.\n" +
                "- 닉네임 변경 시 중복 검사가 수행됩니다.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "프로필 수정 성공 (수정된 정보 반환)",
                content = [Content(schema = Schema(implementation = GetProfileDto::class))],
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
                                value = """{"errorCode": 9001, "message": "닉네임은 필수입니다.", "timestamp": "2025-05-21T10:00:00"}""",
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
                                name = "토큰 만료",
                                value = """{"errorCode": 401, "message": "토큰이 만료되었습니다.", "timestamp": "2025-05-21T10:00:00"}""",
                            ),
                        ],
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "404",
                description = "사용자를 찾을 수 없음",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "유저 없음",
                                value = """{"errorCode": 1001, "message": "해당 유저를 찾을 수 없습니다.", "timestamp": "2025-05-21T10:00:00"}""",
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
                                value = """{"errorCode": 1002, "message": "이미 사용 중인 닉네임입니다.", "timestamp": "2024-05-21T10:00:00"}""",
                            ),
                        ],
                    ),
                ],
            ),
        ],
    )
    @PatchMapping("", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun updateProfile(
        @ModelAttribute updateProfileDto: UpdateProfileDto,
        @Parameter(hidden = true) @LoggedInUser userId: Long,
    ): ResponseEntity<GetProfileDto> {
        val response = profileService.updateProfile(userId, updateProfileDto)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "내 프로필 조회", description = "현재 로그인한 사용자의 프로필 정보를 조회합니다.")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = [Content(schema = Schema(implementation = GetProfileDto::class))],
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
                                name = "토큰 만료",
                                value = """{"errorCode": 401, "message": "토큰이 만료되었습니다.", "timestamp": "2025-05-21T10:00:00"}""",
                            ),
                        ],
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "404",
                description = "사용자를 찾을 수 없음",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "유저 없음",
                                value = """{"errorCode": 1001, "message": "해당 유저를 찾을 수 없습니다.", "timestamp": "2025-05-21T10:00:00"}""",
                            ),
                        ],
                    ),
                ],
            ),
        ],
    )
    @GetMapping("")
    fun getProfile(
        @Parameter(hidden = true) @LoggedInUser userId: Long,
    ): ResponseEntity<GetProfileDto> {
        val response = profileService.getProfile(userId = userId)
        return ResponseEntity.ok(response)
    }

    @Operation(
        summary = "프로필 이미지만 수정",
        description = "다른 정보는 유지하고 프로필 이미지만 별도로 수정합니다.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "이미지 수정 성공",
                content = [Content(schema = Schema(implementation = UpdateProfileImageResponseDto::class))],
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
                                name = "토큰이 유효하지 않음",
                                value = """{"errorCode": 401, "message": "토큰이 유효하지 않습니다.", "timestamp": "2025-05-21T10:00:00"}""",
                            ),
                        ],
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "404",
                description = "사용자를 찾을 수 없음",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "유저 없음",
                                value = """{"errorCode": 1001, "message": "해당 유저를 찾을 수 없습니다.", "timestamp": "2025-05-21T10:00:00"}""",
                            ),
                        ],
                    ),
                ],
            ),
        ],
    )
    @PutMapping(
        "/profile-image",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
    )
    fun updateProfileImage(
        @Parameter(description = "업로드할 이미지 파일")
        @RequestParam("profile_image") profileImage: MultipartFile,
        @Parameter(hidden = true) @LoggedInUser userId: Long,
    ): ResponseEntity<UpdateProfileImageResponseDto> {
        val response = profileService.updateProfileImage(userId, profileImage)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "프로필 이미지 다운로드/리다이렉트")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "302",
                description = "이미지 주소로 이동 (Found)",
                content = [Content(schema = Schema(hidden = true))],
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
                                name = "인증 실패",
                                value = """{"errorCode": 401, "message": "유효하지 않은 토큰입니다.", "timestamp": "2025-05-21T10:00:00"}""",
                            ),
                        ],
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "404",
                description = "프로필 이미지 없음",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "이미지 미설정",
                                summary = "유저는 존재하나 이미지가 없는 경우",
                                value = """{"errorCode": 404, "message": "프로필 이미지가 설정되지 않았습니다.", "timestamp": "2025-05-21T10:00:00"}""",
                            ),
                        ],
                    ),
                ],
            ),
        ],
    )
    @GetMapping("/profile-image")
    fun getProfileImage(
        @Parameter(hidden = true) @LoggedInUser userId: Long,
    ): ResponseEntity<Any> {
        val profileImageUrl = profileService.getProfileImageUrl(userId)
        return if (profileImageUrl != null) {
            ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(profileImageUrl))
                .build()
        } else {
            val errorBody =
                ErrorResponse(
                    errorCode = 404,
                    message = "프로필 이미지가 설정되지 않았습니다.",
                )
            ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorBody)
        }
    }
}
