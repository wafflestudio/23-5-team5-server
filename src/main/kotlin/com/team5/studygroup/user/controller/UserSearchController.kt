package com.team5.studygroup.user.controller

import com.team5.studygroup.common.CursorResponse
import com.team5.studygroup.user.LoggedInUser
import com.team5.studygroup.user.dto.UserSearchResponseDto
import com.team5.studygroup.user.service.UserSearchService
import io.swagger.v3.oas.annotations.Parameter
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users/search")
@Validated
class UserSearchController(
    private val userSearchService: UserSearchService,
) {
    @GetMapping("")
    fun searchUsersInGroup(
        @RequestParam groupId: Long,
        @Parameter(hidden = true) @LoggedInUser requestingUserId: Long,
        @RequestParam(required = false) cursorId: Long?,
        @RequestParam(defaultValue = "10") @Min(1) @Max(50) size: Int,
    ): ResponseEntity<CursorResponse<UserSearchResponseDto>> {
        val result = userSearchService.searchUsersInGroup(groupId, requestingUserId, cursorId, size)
        return ResponseEntity.ok(result)
    }
}
