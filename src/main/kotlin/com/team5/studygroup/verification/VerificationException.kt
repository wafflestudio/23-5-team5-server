package com.team5.studygroup.verification

import com.team5.studygroup.DomainException
import org.springframework.http.HttpStatus

sealed class VerificationException(
    errorCode: Int,
    httpStatusCode: HttpStatus,
    msg: String,
    cause: Throwable? = null,
) : DomainException(errorCode, httpStatusCode, msg, cause)

// 2001: 이메일 도메인 제한
class InvalidEmailDomainException : VerificationException(
    errorCode = 2001,
    httpStatusCode = HttpStatus.BAD_REQUEST,
    msg = "서울대학교 이메일(@snu.ac.kr)만 사용할 수 있습니다.",
)

// 2002: 메일 발송 실패 (서버 오류 or 잘못된 주소)
class EmailSendFailedException : VerificationException(
    errorCode = 2002,
    httpStatusCode = HttpStatus.INTERNAL_SERVER_ERROR,
    msg = "메일 발송에 실패했습니다. 이메일 주소를 확인해주세요.",
)

// 2003: 인증번호 만료 또는 없음
class VerificationCodeExpiredException : VerificationException(
    errorCode = 2003,
    httpStatusCode = HttpStatus.BAD_REQUEST,
    msg = "인증번호가 만료되었거나 발송되지 않았습니다.",
)

// 2004: 인증번호 불일치
class VerificationCodeMismatchException : VerificationException(
    errorCode = 2004,
    httpStatusCode = HttpStatus.BAD_REQUEST,
    msg = "인증번호가 일치하지 않습니다.",
)