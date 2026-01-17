package com.team5.studygroup.user.model

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(private val user: User) : UserDetails {
    // This custom property is how we'll access the user's ID.
    // The SpEL expression `expression = "id"` will resolve to this.

    fun getUserEntity(): User = user

    val id: Long
        get() = user.id!!

    override fun getAuthorities(): Collection<GrantedAuthority> =
        // You would typically load roles from your user object
        listOf(SimpleGrantedAuthority(user.userRole.key))

    override fun getPassword(): String? = user.password

    override fun getUsername(): String = user.username

    // For simplicity, we'll return true for these.
    // In a real app, you might have logic from your User object.
    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = user.isVerified
}
