package com.team5.studygroup.group.service

class SearchService {
    fun searchAll(): String {
        return "전체 스터디그룹 조회"
    }

    fun searchByCategory(categoryId: Int): String {
        return "$categoryId 카테고리 스터디그룹 조회"
    }

    fun searchByKeyword(keyword: String): String {
        return "$keyword 키워드 스터디그룹 조회"
    }
}
