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
        keyword: String?,
    ): List<GroupResponse> {
        val groups =
            when {
                categoryId != null && keyword != null -> {
                    groupRepository.findByCategoryIdAndKeyword(categoryId, keyword)
                }
                categoryId != null -> {
                    groupRepository.findByCategoryId(categoryId)
                }
                keyword != null -> {
                    groupRepository.findByGroupNameContainingOrDescriptionContaining(keyword, keyword)
                }
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
