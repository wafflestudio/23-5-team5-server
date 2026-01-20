package com.team5.studygroup.user.repository

import com.team5.studygroup.user.model.ProviderType
import com.team5.studygroup.user.model.SocialAuth
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SocialAuthRepository : JpaRepository<SocialAuth, Long> {
    fun findByProviderAndProviderId(
        provider: ProviderType,
        providerId: String,
    ): SocialAuth?

    fun existsByProviderAndProviderId(
        provider: ProviderType,
        providerId: String,
    ): Boolean
}
