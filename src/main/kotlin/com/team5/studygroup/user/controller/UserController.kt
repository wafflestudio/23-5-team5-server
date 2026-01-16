package com.team5.studygroup.user.controller

import com.team5.studygroup.user.LoggedInUser
import com.team5.studygroup.user.dto.CreateProfileDto
import com.team5.studygroup.user.dto.GetProfileDto
import com.team5.studygroup.user.dto.UpdateProfileDto
import com.team5.studygroup.user.service.ProfileService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users/me")
class UserController(
    private val profileService: ProfileService,
) {
    /**
     * POST 최초 프로필 생성 (회원가입/인증 후 단계)
     * 학번이 포함되며, 이후 수정이 불가능함
     */
    @PostMapping("")
    fun createProfile(
        @RequestBody createProfileDto: CreateProfileDto,
        @LoggedInUser userId: Long,
    ): ResponseEntity<GetProfileDto> {
        val response = profileService.createProfile(userId, createProfileDto)
        return ResponseEntity.ok(response)
    }

    // 프로필 수정
    @PatchMapping("")
    fun updateProfile(
        @RequestBody updateProfileDto: UpdateProfileDto,
        @LoggedInUser userId: Long,
    ): ResponseEntity<GetProfileDto> {
        val response = profileService.updateProfile(userId, updateProfileDto)
        return ResponseEntity.ok(response)
    }

    // 프로필 조회
    @GetMapping("")
    fun getProfile(
        @LoggedInUser userId: Long,
    ): ResponseEntity<GetProfileDto> {
        val response = profileService.getProfile(userId = userId)
        return ResponseEntity.ok(response)
    }
}
