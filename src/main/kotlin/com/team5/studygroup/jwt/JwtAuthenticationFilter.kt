package com.team5.studygroup.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import com.team5.studygroup.common.ErrorResponse
import com.team5.studygroup.user.UserNotFoundException
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val objectMapper: ObjectMapper,
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
            setErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "토큰이 만료되었습니다.")
            return
        } catch (e: JwtException) {
            setErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 토큰입니다.")
            return
        } catch (e: UserNotFoundException) {
            setErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "해당 유저를 찾을 수 없습니다.")
            return
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
        status: Int,
        message: String,
    ) {
        response.status = status
        response.contentType = MediaType.APPLICATION_JSON_VALUE // "application/json"
        response.characterEncoding = "UTF-8"

        // 정의해둔 ErrorResponse 객체 생성
        val errorResponse =
            ErrorResponse(
                errorCode = status,
                message = message,
            )

        // ObjectMapper를 사용해 객체를 JSON 문자열로 변환하여 응답에 쓰기
        objectMapper.writeValue(response.writer, errorResponse)
    }
}
