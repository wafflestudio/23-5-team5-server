package com.team5.studygroup.user

import com.team5.studygroup.DomainException
import org.springframework.http.HttpStatus

sealed class UserException(
    errorCode: Int,
    httpStatusCode: HttpStatus,
    msg: String,
    cause: Throwable? = null,
) : DomainException(errorCode, httpStatusCode, msg, cause)

class UserNotFoundException : UserException(
    // 유저 관련 에러는 1000번대 등으로 규칙을 정하면 좋습니다
    errorCode = 1001,
    httpStatusCode = HttpStatus.NOT_FOUND,
    msg = "해당 유저를 찾을 수 없습니다.",
)

class NicknameDuplicateException : UserException(
    errorCode = 1002,
    httpStatusCode = HttpStatus.CONFLICT,
    msg = "이미 사용 중인 닉네임입니다.",
)

// 학번 중복 예외
class StudentNumberDuplicateException : UserException(
    errorCode = 1003,
    httpStatusCode = HttpStatus.CONFLICT,
    msg = "이미 등록된 학번입니다.",
)
