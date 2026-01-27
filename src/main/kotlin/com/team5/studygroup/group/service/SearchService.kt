package com.team5.studygroup.group.service

import GroupResponse
import com.team5.studygroup.group.repository.GroupRepository
import org.springframework.stereotype.Service

@Service
class SearchService(
    private val groupRepository: GroupRepository,
) {
    // 전체 조회 + 카테고리 + 키워드 통합
    fun search(
        categoryId: Long?,
        subCategoryId: Long?,
        keyword: String?,
    ): List<GroupResponse> {
        val groups =
            when {
                // 서브카테고리 + 키워드
                subCategoryId != null && keyword != null -> {
                    groupRepository.findBySubCategoryIdAndKeyword(subCategoryId, keyword)
                }
                // 서브카테고리만
                subCategoryId != null -> {
                    groupRepository.findBySubCategoryId(subCategoryId)
                }
                // 카테고리 + 키워드
                categoryId != null && keyword != null -> {
                    groupRepository.findByCategoryIdAndKeyword(categoryId, keyword)
                }
                // 카테고리만
                categoryId != null -> {
                    groupRepository.findByCategoryId(categoryId)
                }
                // 키워드만
                // 키워드 검색은 카테고리나 서브카테고리는 검색하지 않음
                keyword != null -> {
                    groupRepository.findByGroupNameContainingOrDescriptionContaining(keyword, keyword)
                }
                // 전체
                else -> {
                    groupRepository.findAll()
                }
            }
        return groups.map { GroupResponse.from(it) }
    }

    // 내가 작성한 공고
    fun searchMyGroup(userId: Long): List<GroupResponse> {
        val groups = groupRepository.findByLeaderId(userId)
        return groups.map { GroupResponse.from(it) }
    }
}
