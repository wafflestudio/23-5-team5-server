package com.team5.studygroup.user.service

import com.team5.studygroup.user.model.CustomUserDetails
import com.team5.studygroup.user.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository,
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails =
        userRepository.findByUsername(username)
            ?.let { user -> CustomUserDetails(user) }
            ?: throw UsernameNotFoundException("사용자($username)를 찾을 수 없습니다.")
}
