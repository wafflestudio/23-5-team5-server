package com.team5.studygroup.user.controller

import com.team5.studygroup.user.LoggedInUser
import com.team5.studygroup.user.dto.GetProfileDto
import com.team5.studygroup.user.dto.PostProfileDto
import com.team5.studygroup.user.dto.UpdateProfileDto
import com.team5.studygroup.user.service.ProfileService
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
    @PostMapping("")
    fun postProfile(
        @RequestBody postProfileDto: PostProfileDto,
        @LoggedInUser userId: Long,
    ): String {
        return profileService.postProfile(
            userId = userId,
            major = postProfileDto.major,
            nickname = postProfileDto.nickname,
            profileImageUrl = postProfileDto.profileImageUrl,
            bio = postProfileDto.bio,
        )
    }

    @PatchMapping("")
    fun updateProfile(
        @RequestBody updateProfileDto: UpdateProfileDto,
        @LoggedInUser userId: Long,
    ): String {
        return profileService.updateProfile(
            userId = userId,
            major = updateProfileDto.major,
            nickname = updateProfileDto.nickname,
            profileImageUrl = updateProfileDto.profileImageUrl,
            bio = updateProfileDto.bio,
        )
    }

    @GetMapping("")
    fun getProfile(
        @RequestBody getProfileDto: GetProfileDto,
        @LoggedInUser userId: Long,
    ): String {
        return profileService.getProfile(userId = userId)
    }
}
