package com.team5.studygroup.user.controller

import com.team5.studygroup.user.LoggedInUser
import com.team5.studygroup.user.dto.GetProfileDto
import com.team5.studygroup.user.dto.UpdateProfileDto
import com.team5.studygroup.user.service.ProfileService
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
}
