package com.team5.studygroup.jwt

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {
            val token = resolveToken(request)
            if (token != null) {
                if (jwtTokenProvider.validateToken(token)) {
                    val authentication = jwtTokenProvider.getAuthentication(token)
                    SecurityContextHolder.getContext().authentication = authentication
                }
            }
        } catch (e: ExpiredJwtException) {
            setErrorResponse(response, "토큰이 만료되었습니다.", HttpServletResponse.SC_UNAUTHORIZED)
            return // 에러 응답 후 종료
        } catch (e: JwtException) {
            setErrorResponse(response, "유효하지 않은 토큰입니다.", HttpServletResponse.SC_UNAUTHORIZED)
            return // 에러 응답 후 종료
        }

        filterChain.doFilter(request, response)
    }

    // ... 나머지 resolveToken, setErrorResponse 코드는 그대로 유지 ...
    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        return if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else {
            null
        }
    }

    private fun setErrorResponse(
        response: HttpServletResponse,
        message: String,
        status: Int,
    ) {
        response.status = status
        response.contentType = "application/json;charset=UTF-8"
        response.writer.write("""{"status": $status, "error": "Unauthorized", "message": "$message"}""")
    }
}
