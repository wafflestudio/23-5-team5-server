package com.team5.studygroup.common

import com.team5.studygroup.DomainException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

// @RestControllerAdvice // 모든 RestController의 예외를 여기서 가로챔
class GlobalExceptionHandler {
    @ExceptionHandler(DomainException::class)
    fun handleDomainException(e: DomainException): ResponseEntity<ErrorResponse> {
        val errorResponse =
            ErrorResponse(
                errorCode = e.errorCode,
                // DomainException에 정의된 msg 사용
                message = e.msg,
            )

        return ResponseEntity
            // 우리가 예외 클래스에 설정한 HTTP 상태 코드 (404, 409 등)
            .status(e.httpErrorCode)
            .body(errorResponse)
    }

    // 그 외 예상치 못한 에러(500 에러 등) 처리
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ErrorResponse> {
        val errorResponse =
            ErrorResponse(
                errorCode = 9999,
                message = "서버 내부 에러가 발생했습니다. 관리자에게 문의하세요.",
            )
        return ResponseEntity.status(500).body(errorResponse)
    }
}
