package com.team5.studygroup.group.controller

import GroupResponse
import com.team5.studygroup.group.service.SearchService
import com.team5.studygroup.user.LoggedInUser
import com.team5.studygroup.user.model.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/groups/search")
class SearchController(
    private val searchService: SearchService,
) {
    @GetMapping("")
    fun search(
        @RequestParam(required = false) categoryId: Long?,
        @RequestParam(required = false) subCategoryId: Long?,
        @RequestParam(required = false) keyword: String?,
        // 우선 페이지당 항목은 10개, 정렬 기준은 createdAt으로 설정
        @PageableDefault(size = 10, sort = ["createdAt"]) pageable: Pageable,
    ): ResponseEntity<Page<GroupResponse>> {
        val result = searchService.search(categoryId, subCategoryId, keyword, pageable)
        return ResponseEntity.ok(result)
    }

    @GetMapping("/me")
    fun searchMyGroup(
        @LoggedInUser user: User,
        @PageableDefault(size = 10, sort = ["createdAt"]) pageable: Pageable,
    ): ResponseEntity<Page<GroupResponse>> {
        val result = searchService.searchMyGroup(user.id!!, pageable)
        return ResponseEntity.ok(result)
    }
}
