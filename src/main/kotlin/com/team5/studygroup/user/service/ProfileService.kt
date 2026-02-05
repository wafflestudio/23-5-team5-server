package com.team5.studygroup.user.service

import com.team5.studygroup.common.S3Service
import com.team5.studygroup.user.NicknameDuplicateException
import com.team5.studygroup.user.UserNotFoundException
import com.team5.studygroup.user.dto.GetProfileDto
import com.team5.studygroup.user.dto.UpdateProfileDto
import com.team5.studygroup.user.dto.UpdateProfileImageResponseDto
import com.team5.studygroup.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class ProfileService(
    private val userRepository: UserRepository,
    private val s3Service: S3Service,
) {
    /**
     * 프로필 수정 (학번 수정 불가)
     * 수정된 결과를 GetProfileDto로 반환하여 즉시 클라이언트가 반영할 수 있게 함
     */
    @Transactional
    fun updateProfile(
        userId: Long,
        dto: UpdateProfileDto,
    ): GetProfileDto { // 반환 타입을 GetProfileDto로 변경
        // 1. 사용자 조회
        val user =
            userRepository.findById(userId)
                .orElseThrow { UserNotFoundException() }

        dto.nickname?.let { newNickname ->
            // 입력받은 닉네임이 현재 내 닉네임과 다를 때만 중복 검사 실행
            if (newNickname != user.nickname) {
                if (userRepository.existsByNickname(newNickname)) {
                    throw NicknameDuplicateException() // 아까 만든 커스텀 예외
                }
            }
        }

        val oldProfileImageUrl = user.profileImageUrl
        var newProfileImageUrl: String? = null

        dto.profile_image?.let { file ->
            if (!file.isEmpty) {
                newProfileImageUrl = s3Service.upload(file, "profile")
            }
        }

        user.updateProfile(
            major = dto.major,
            nickname = dto.nickname,
            profileImageUrl = newProfileImageUrl ?: user.profileImageUrl,
            bio = dto.bio,
        )

        if (newProfileImageUrl != null && oldProfileImageUrl != null) {
            s3Service.delete(oldProfileImageUrl)
        }

        return GetProfileDto.fromEntity(user)
    }

    /**
     * 프로필 조회
     */
    @Transactional(readOnly = true)
    fun getProfile(userId: Long): GetProfileDto {
        val user =
            userRepository.findById(userId)
                .orElseThrow { UserNotFoundException() }

        return GetProfileDto.fromEntity(user)
    }

    @Transactional
    fun updateProfileImage(
        userId: Long,
        profileImage: MultipartFile,
    ): UpdateProfileImageResponseDto {
        val user =
            userRepository.findById(userId)
                .orElseThrow { UserNotFoundException() }

        val oldProfileImageUrl = user.profileImageUrl

        val newProfileImageUrl =
            if (!profileImage.isEmpty) {
                s3Service.upload(profileImage, "profile")
            } else {
                null
            }

        if (newProfileImageUrl != null) {
            user.updateProfile(profileImageUrl = newProfileImageUrl)

            if (oldProfileImageUrl != null) {
                s3Service.delete(oldProfileImageUrl)
            }
        }

        return UpdateProfileImageResponseDto(
            username = user.username,
            profileImageUrl = user.profileImageUrl,
            createdAt = user.updatedAt,
        )
    }

    @Transactional(readOnly = true)
    fun getProfileImageUrl(userId: Long): String? {
        val user =
            userRepository.findById(userId)
                .orElseThrow { UserNotFoundException() }
        return user.profileImageUrl
    }
}
