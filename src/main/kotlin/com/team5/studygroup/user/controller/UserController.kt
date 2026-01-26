package com.team5.studygroup.user.controller

import com.team5.studygroup.user.LoggedInUser
import com.team5.studygroup.user.dto.GetProfileDto
import com.team5.studygroup.user.dto.UpdateProfileDto
import com.team5.studygroup.user.service.ProfileService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
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
                responseCode = "404",
                description = "사용자를 찾을 수 없음",
                content = [Content(schema = Schema(hidden = true))],
            ),
            ApiResponse(
                responseCode = "409",
                description = "이미 사용 중인 닉네임",
                content = [Content(schema = Schema(hidden = true))],
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
                responseCode = "404",
                description = "사용자를 찾을 수 없음",
                content = [Content(schema = Schema(hidden = true))],
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
                content = [Content(schema = Schema(implementation = GetProfileDto::class))],
            ),
            ApiResponse(
                responseCode = "404",
                description = "사용자를 찾을 수 없음",
                content = [Content(schema = Schema(hidden = true))],
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
    ): ResponseEntity<GetProfileDto> {
        val response = profileService.updateProfileImage(userId, profileImage)
        return ResponseEntity.ok(response)
    }

    // 프로필 이미지 다운로드
    @GetMapping("/profile-image")
    fun getProfileImage(
        @Parameter(hidden = true) @LoggedInUser userId: Long,
    ): ResponseEntity<Void> {
        val profileImageUrl = profileService.getProfileImageUrl(userId)
        return if (profileImageUrl != null) {
            ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(profileImageUrl))
                .build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
