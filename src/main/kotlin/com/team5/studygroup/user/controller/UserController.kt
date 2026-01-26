package com.team5.studygroup.user.controller

import com.team5.studygroup.user.LoggedInUser
import com.team5.studygroup.user.dto.GetProfileDto
import com.team5.studygroup.user.dto.UpdateProfileDto
import com.team5.studygroup.user.dto.UpdateProfileImageResponseDto
import com.team5.studygroup.user.service.ProfileService
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.http.HttpStatus
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

@RestController
@RequestMapping("/users/me")
class UserController(
    private val profileService: ProfileService,
) {
    // 프로필 수정
    @PatchMapping("", consumes = ["multipart/form-data"])
    fun updateProfile(
        @ModelAttribute updateProfileDto: UpdateProfileDto,
        @Parameter(hidden = true) @LoggedInUser userId: Long,
    ): ResponseEntity<GetProfileDto> {
        val response = profileService.updateProfile(userId, updateProfileDto)
        return ResponseEntity.ok(response)
    }

    // 프로필 조회
    @GetMapping("")
    fun getProfile(
        @Parameter(hidden = true) @LoggedInUser userId: Long,
    ): ResponseEntity<GetProfileDto> {
        val response = profileService.getProfile(userId = userId)
        return ResponseEntity.ok(response)
    }

    // 프로필 이미지 수정
    @PutMapping("/profile-image", consumes = ["multipart/form-data"])
    fun updateProfileImage(
        @RequestParam("profile_image") profileImage: MultipartFile,
        @Parameter(hidden = true) @LoggedInUser userId: Long,
    ): ResponseEntity<UpdateProfileImageResponseDto> {
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
