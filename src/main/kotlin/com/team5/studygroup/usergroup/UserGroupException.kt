package com.team5.studygroup.usergroup

import com.team5.studygroup.DomainException
import org.springframework.http.HttpStatus

sealed class UserGroupException(
    errorCode: Int,
    httpStatusCode: HttpStatus,
    msg: String,
    cause: Throwable? = null,
) : DomainException(errorCode, httpStatusCode, msg, cause)

class GroupNotFoundException : UserGroupException(
    errorCode = 3001,
    httpStatusCode = HttpStatus.NOT_FOUND,
    msg = "해당 그룹을 찾을 수 없습니다.",
)

class UserGroupDuplicateException : UserGroupException(
    errorCode = 3002,
    httpStatusCode = HttpStatus.CONFLICT,
    msg = "이미 가입한 그룹입니다.",
)

class UserGroupNotFoundException : UserGroupException(
    errorCode = 3003,
    httpStatusCode = HttpStatus.NOT_FOUND,
    msg = "가입한 그룹이 아닙니다.",
)

class UserGroupDeleteForbiddenException : UserGroupException(
    errorCode = 3004,
    httpStatusCode = HttpStatus.UNAUTHORIZED,
    msg = "본인이 가입한 그룹만 탈퇴할 수 있습니다.",
)

class GroupNotRecruitingException : UserGroupException(
    errorCode = 3005,
    httpStatusCode = HttpStatus.BAD_REQUEST,
    msg = "현재 모집 중인 그룹이 아닙니다.",
)

class GroupFullException : UserGroupException(
    errorCode = 3006,
    httpStatusCode = HttpStatus.BAD_REQUEST,
    msg = "그룹의 정원이 가득 차서 가입할 수 없습니다.",
)
