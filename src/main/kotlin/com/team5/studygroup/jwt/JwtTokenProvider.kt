package com.team5.studygroup.jwt

import com.team5.studygroup.user.service.CustomUserDetailsService
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.Date

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}")
    private val secretKey: String,
    @Value("\${jwt.expiration-time}")
    private val expirationInMs: Long,
    private val userDetailsService: CustomUserDetailsService,
) {
    // [수정됨] Base64 디코딩 제거 -> 일반 문자열을 바이트로 변환
    private val key by lazy {
        Keys.hmacShaKeyFor(secretKey.toByteArray(StandardCharsets.UTF_8))
    }

    // ... 아래 코드는 그대로 두셔도 됩니다 ...

    fun createToken(username: String): String {
        val now = Date()
        val validity = Date(now.time + expirationInMs)

        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    // ... 나머지 함수들 (getUsername, getAuthentication, validateToken, getClaims) 그대로 유지 ...
    fun getUsername(token: String): String = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).body.subject

    fun getAuthentication(token: String): Authentication {
        val username = getUsername(token)
        val userDetails = userDetailsService.loadUserByUsername(username)
        return UsernamePasswordAuthenticationToken(userDetails, token, userDetails.authorities)
    }

    fun validateToken(token: String): Boolean {
        getClaims(token)
        return true
    }

    private fun getClaims(token: String): Claims {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).body
    }
}
