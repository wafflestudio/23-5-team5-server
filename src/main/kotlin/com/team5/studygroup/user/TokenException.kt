package com.team5.studygroup.user

import com.team5.studygroup.DomainException
import org.springframework.http.HttpStatus

sealed class TokenException(
    errorCode: Int,
    httpStatusCode: HttpStatus,
    msg: String,
    cause: Throwable? = null,
) : DomainException(errorCode, httpStatusCode, msg, cause)

/**
 * ErrorCode: 2001
 * 상황: 이미 로그아웃(블랙리스트) 처리된 토큰으로 요청한 경우
 */
class AlreadyLoggedOutException : TokenException(
    errorCode = 2001,
    httpStatusCode = HttpStatus.UNAUTHORIZED,
    msg = "이미 로그아웃된 토큰입니다.",
)

/**
 * ErrorCode: 2002
 * 상황: 헤더에 Bearer가 없거나 토큰 형식이 깨진 경우
 */
class InvalidTokenFormatException : TokenException(
    errorCode = 2002,
    httpStatusCode = HttpStatus.UNAUTHORIZED,
    msg = "토큰 형식이 올바르지 않습니다.",
)
