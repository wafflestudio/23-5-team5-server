package com.team5.studygroup.group.controller

import GroupResponse
import com.team5.studygroup.group.service.SearchService
import com.team5.studygroup.user.LoggedInUser
import com.team5.studygroup.user.model.User
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
    ): ResponseEntity<List<GroupResponse>> {
        val result = searchService.search(categoryId, subCategoryId, keyword)
        return ResponseEntity.ok(result)
    }

    @GetMapping("/me")
    fun searchMyGroup(
        @LoggedInUser user: User,
    ): ResponseEntity<List<GroupResponse>> {
        val result = searchService.searchMyGroup(user.id!!)
        return ResponseEntity.ok(result)
    }
}
