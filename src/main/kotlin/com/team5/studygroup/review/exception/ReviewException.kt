package com.team5.studygroup.review.exception

import com.team5.studygroup.DomainException
import org.springframework.http.HttpStatus

sealed class ReviewException(
    errorCode: Int,
    httpStatusCode: HttpStatus,
    msg: String,
    cause: Throwable? = null,
) : DomainException(errorCode, httpStatusCode, msg, cause)

// 리뷰를 찾을 수 없을 때
class ReviewNotFoundException : ReviewException(
    errorCode = 4001,
    httpStatusCode = HttpStatus.NOT_FOUND,
    msg = "해당 리뷰를 찾을 수 없습니다.",
)

// 본인이 작성한 리뷰가 아닌 경우
class ReviewDeleteForbiddenException : ReviewException(
    errorCode = 4002,
    httpStatusCode = HttpStatus.UNAUTHORIZED,
    msg = "본인이 작성한 리뷰만 삭제할 수 있습니다.",
)

class ReviewUpdateForbiddenException : ReviewException(
    errorCode = 4003,
    httpStatusCode = HttpStatus.UNAUTHORIZED,
    msg = "본인이 작성한 리뷰만 삭제할 수 있습니다.",
)
