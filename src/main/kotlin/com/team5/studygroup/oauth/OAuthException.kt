package com.team5.studygroup.oauth

import com.team5.studygroup.DomainException
import org.springframework.http.HttpStatus

sealed class OAuthException(
    errorCode: Int,
    httpStatusCode: HttpStatus,
    msg: String,
    cause: Throwable? = null,
) : DomainException(errorCode, httpStatusCode, msg, cause) {
    // 토큰 자체가 유효하지 않거나 만료된 경우 (401)
    class InvalidRegisterToken(cause: Throwable? = null) : OAuthException(
        errorCode = 4001,
        httpStatusCode = HttpStatus.UNAUTHORIZED,
        msg = "유효하지 않거나 만료된 가입 토큰입니다.",
        cause,
    )

    // 토큰의 용도(subject)가 맞지 않는 경우 (400)
    class InvalidTokenSubject : OAuthException(
        errorCode = 4002,
        httpStatusCode = HttpStatus.BAD_REQUEST,
        msg = "회원가입 전용 토큰이 아닙니다.",
    )

    // 요청한 Provider와 토큰 내 Provider가 다른 경우 (400)
    class ProviderMismatch(requested: String, token: String) : OAuthException(
        errorCode = 4003,
        httpStatusCode = HttpStatus.BAD_REQUEST,
        msg = "잘못된 접근입니다. (Provider 불일치: 요청=$requested, 토큰=$token)",
    )

    // 이메일 도메인 제한 (403)
    class InvalidEmailDomain : OAuthException(
        errorCode = 4004,
        httpStatusCode = HttpStatus.FORBIDDEN,
        msg = "서울대 이메일(@snu.ac.kr)만 가입 가능합니다.",
    )
}
