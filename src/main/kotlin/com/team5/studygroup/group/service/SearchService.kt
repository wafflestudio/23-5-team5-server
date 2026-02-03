package com.team5.studygroup.group.service

import GroupResponse
import com.team5.studygroup.group.repository.GroupRepository
import com.team5.studygroup.usergroup.repository.UserGroupRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class SearchService(
    private val groupRepository: GroupRepository,
    private val userGroupRepository: UserGroupRepository,
) {
    // 전체 조회 + 카테고리 + 키워드 통합
    fun search(
        categoryId: Long?,
        subCategoryId: Long?,
        keyword: String?,
        pageable: Pageable,
    ): Page<GroupResponse> {
        val groups =
            when {
                // 서브카테고리 + 키워드
                subCategoryId != null && keyword != null -> {
                    groupRepository.findBySubCategoryIdAndKeyword(subCategoryId, keyword, pageable)
                }
                // 서브카테고리만
                subCategoryId != null -> {
                    groupRepository.findBySubCategoryId(subCategoryId, pageable)
                }
                // 카테고리 + 키워드
                categoryId != null && keyword != null -> {
                    groupRepository.findByCategoryIdAndKeyword(categoryId, keyword, pageable)
                }
                // 카테고리만
                categoryId != null -> {
                    groupRepository.findByCategoryId(categoryId, pageable)
                }
                // 키워드만
                // 키워드 검색은 카테고리나 서브카테고리는 검색하지 않음
                keyword != null -> {
                    groupRepository.findByGroupNameContainingOrDescriptionContaining(keyword, keyword, pageable)
                }
                // 전체
                else -> {
                    groupRepository.findAll(pageable)
                }
            }
        return groups.map { GroupResponse.from(it) }
    }

    // 내가 작성한 공고
    fun searchMyGroup(
        userId: Long,
        pageable: Pageable,
    ): Page<GroupResponse> {
        val groups = groupRepository.findByLeaderId(userId, pageable)
        return groups.map { GroupResponse.from(it) }
    }

    // 내가 참여한 스터디 그룹
    fun searchJoinedGroup(
        userId: Long,
        pageable: Pageable,
    ): Page<GroupResponse> {
        val userGroups = userGroupRepository.findByUserId(userId)
        val groupIds = userGroups.map { it.groupId }
        val groups = groupRepository.findByIdIn(groupIds, pageable)
        return groups.map { GroupResponse.from(it) }
    }
}
