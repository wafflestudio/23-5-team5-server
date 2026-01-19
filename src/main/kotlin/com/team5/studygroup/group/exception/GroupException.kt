package com.team5.studygroup.group.exception

import com.team5.studygroup.DomainException
import org.springframework.http.HttpStatus

sealed class GroupException(
    errorCode: Int,
    httpStatusCode: HttpStatus,
    msg: String,
    cause: Throwable? = null,
) : DomainException(errorCode, httpStatusCode, msg, cause)

// 그룹을 찾을 수 없을 때
class GroupNotFoundException : GroupException(
    // 그룹 관련 에러는 2000번대로 규칙을 정하면 좋습니다
    errorCode = 2001,
    httpStatusCode = HttpStatus.NOT_FOUND,
    msg = "해당 스터디 그룹을 찾을 수 없습니다.",
)

// 그룹장이 아닌 사용자가 권한이 필요한 작업을 시도할 때
class NotGroupLeaderException : GroupException(
    errorCode = 2002,
    httpStatusCode = HttpStatus.FORBIDDEN,
    msg = "스터디 그룹장만 이 작업을 수행할 수 있습니다.",
)

class GroupAlreadyExpiredException : GroupException(
    errorCode = 2003,
    httpStatusCode = HttpStatus.CONFLICT,
    msg = "이미 만료된 스터디 그룹입니다.",
)
