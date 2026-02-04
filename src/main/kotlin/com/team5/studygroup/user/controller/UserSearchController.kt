package com.team5.studygroup.user.controller

import com.team5.studygroup.user.LoggedInUser
import com.team5.studygroup.user.dto.UserSearchResponseDto
import com.team5.studygroup.user.service.UserSearchService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users/search")
class UserSearchController(
    private val userSearchService: UserSearchService,
) {
    @GetMapping("")
    fun searchUsersInGroup(
        @RequestParam groupId: Long,
        @LoggedInUser requestingUserId: Long,
        @PageableDefault(size = 10, sort = ["createdAt"]) pageable: Pageable,
    ): ResponseEntity<Page<UserSearchResponseDto>> {
        val result = userSearchService.searchUsersInGroup(groupId, requestingUserId, pageable)
        return ResponseEntity.ok(result)
    }
}
