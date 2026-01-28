package com.team5.studygroup.common

import com.team5.studygroup.DomainException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    // 커스텀 예외 처리 (기존)
    @ExceptionHandler(DomainException::class)
    fun handleDomainException(e: DomainException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            errorCode = e.errorCode,
            message = e.msg,
        )
        return ResponseEntity
            .status(e.httpErrorCode)
            .body(errorResponse)
    }

    // DTO Validation 에러 처리 (추가)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val message = e.bindingResult.fieldErrors
            .firstOrNull()?.defaultMessage ?: "입력값이 올바르지 않습니다."

        val errorResponse = ErrorResponse(
            errorCode = 9001,
            message = message,
        )
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errorResponse)
    }

    // 그 외 예상치 못한 에러 (500)
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            errorCode = 9999,
            message = "서버 내부 에러가 발생했습니다. 관리자에게 문의하세요.",
        )
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(errorResponse)
    }
}