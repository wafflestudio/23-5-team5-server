package com.team5.studygroup.group.service

import com.team5.studygroup.common.CursorResponse
import com.team5.studygroup.group.dto.GroupResponse
import com.team5.studygroup.group.model.Group
import com.team5.studygroup.group.repository.GroupRepository
import com.team5.studygroup.usergroup.repository.UserGroupRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class SearchService(
    private val groupRepository: GroupRepository,
    private val userGroupRepository: UserGroupRepository,
) {
    // 공통 로직: size + 1개 조회 후 CursorResponse로 변환 (변경 없음)
    private fun makeCursorResponse(
        groups: List<Group>,
        size: Int,
    ): CursorResponse<GroupResponse> {
        var hasNext = false
        val resultList = ArrayList(groups)

        if (resultList.size > size) {
            hasNext = true
            resultList.removeAt(size)
        }

        val nextCursorId = resultList.lastOrNull()?.id

        return CursorResponse(
            content = resultList.map { GroupResponse.from(it) },
            nextCursorId = nextCursorId,
            hasNext = hasNext,
        )
    }

    // 통합 검색
    fun search(
        categoryId: Long?,
        subCategoryId: Long?,
        keyword: String?,
        cursorId: Long?,
        size: Int,
    ): CursorResponse<GroupResponse> {
        // 커서 기반 페이징을 위한 size + 1 설정
        val pageable = PageRequest.of(0, size + 1)

        // 키워드가 있으면 앞뒤로 % 붙이기 (LIKE 검색용), 없으면 null 그대로 전달
        val searchKeyword = keyword?.let { "%$it%" }

        // 하나의 통합 메서드로 모든 조건 처리
        val groups =
            groupRepository.searchWithConditions(
                categoryId = categoryId,
                subCategoryId = subCategoryId,
                keyword = searchKeyword,
                cursorId = cursorId,
                pageable = pageable,
            )

        return makeCursorResponse(groups, size)
    }

    // 내가 작성한 공고
    fun searchMyGroup(
        userId: Long,
        cursorId: Long?,
        size: Int,
    ): CursorResponse<GroupResponse> {
        val pageable = PageRequest.of(0, size + 1)
        val groups = groupRepository.findByLeaderIdAndCursor(userId, cursorId, pageable)

        return makeCursorResponse(groups, size)
    }

    // 내가 참여한 스터디 그룹
    fun searchJoinedGroup(
        userId: Long,
        cursorId: Long?,
        size: Int,
    ): CursorResponse<GroupResponse> {
        val pageable = PageRequest.of(0, size + 1)

        val userGroups = userGroupRepository.findByUserId(userId)
        val groupIds = userGroups.map { it.groupId }

        if (groupIds.isEmpty()) {
            return CursorResponse(emptyList(), null, false)
        }

        val groups = groupRepository.findByIdInAndCursor(groupIds, cursorId, pageable)

        return makeCursorResponse(groups, size)
    }
}
