package com.team5.studygroup.group.controller

import com.team5.studygroup.group.service.SearchService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/groups/search")
class SearchController(
    private val searchService: SearchService,
) {
    @GetMapping("")
    fun searchAll(): String {
        return searchService.searchAll()
    }

    @GetMapping("/{categoryId}")
    fun searchByCategory(
        @PathVariable("categoryId") categoryId: Int,
    ): String {
        return searchService.searchByCategory(categoryId)
    }

    @GetMapping("/{keyword}")
    fun searchByKeyword(
        @PathVariable("keyword") keyword: String,
    ): String {
        return searchService.searchByKeyword(keyword)
    }
}
