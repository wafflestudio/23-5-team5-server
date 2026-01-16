package com.team5.studygroup.user.service

import com.team5.studygroup.user.NicknameDuplicateException
import com.team5.studygroup.user.StudentNumberDuplicateException
import com.team5.studygroup.user.UserNotFoundException
import com.team5.studygroup.user.dto.CreateProfileDto
import com.team5.studygroup.user.dto.GetProfileDto
import com.team5.studygroup.user.dto.UpdateProfileDto
import com.team5.studygroup.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProfileService(
    private val userRepository: UserRepository,
) {
    /**
     * 최초 프로필 등록 (학번 입력 필수, 중복 검사)
     */
    @Transactional
    fun createProfile(
        userId: Long,
        dto: CreateProfileDto,
    ): GetProfileDto {
        // 1. 사용자 조회
        val user =
            userRepository.findById(userId)
                .orElseThrow { UserNotFoundException() }

        // 1. 학번 중복 체크 (최초 등록 시에만 수행)
        if (userRepository.existsByStudentNumber(dto.studentNumber)) {
            throw StudentNumberDuplicateException()
        }

        // 2. 닉네임 중복 체크
        if (userRepository.existsByNickname(dto.nickname)) {
            throw NicknameDuplicateException()
        }

        // 3. 전체 정보 업데이트
        user.registerProfile(
            studentNumber = dto.studentNumber,
            major = dto.major,
            nickname = dto.nickname,
            profileImageUrl = dto.profileImageUrl,
            bio = dto.bio,
        )

        return GetProfileDto.fromEntity(user)
    }

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

        // 2. 엔티티 내부 메서드 호출 (Dirty Checking에 의해 자동 저장됨)
        user.updateProfile(
            major = dto.major,
            nickname = dto.nickname,
            profileImageUrl = dto.profileImageUrl,
            bio = dto.bio,
        )

        // 3. 수정된 유저 정보를 DTO로 변환하여 반환
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
}
