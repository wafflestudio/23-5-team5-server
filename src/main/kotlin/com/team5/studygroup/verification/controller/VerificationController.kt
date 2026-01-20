package com.team5.studygroup.verification.controller

import com.team5.studygroup.oauth.dto.OAuthLoginResponse
import com.team5.studygroup.verification.dto.EmailRequest
import com.team5.studygroup.verification.dto.VerifyRequest
import com.team5.studygroup.verification.service.VerificationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class VerificationController(
    private val verificationService: VerificationService,
) {
    @PostMapping("/code")
    fun sendCode(
        @RequestBody request: EmailRequest,
    ): ResponseEntity<String> {
        verificationService.sendCode(request.email)
        return ResponseEntity.ok("인증번호가 발송되었습니다. (유효시간 3분)")
    }

    @PostMapping("/verify")
    fun verify(
        @RequestBody request: VerifyRequest,
    ): ResponseEntity<OAuthLoginResponse> {
        val response = verificationService.verifyAndProcess(request)
        return ResponseEntity.ok(response)
    }
}
