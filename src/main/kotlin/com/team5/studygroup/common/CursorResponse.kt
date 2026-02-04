package com.team5.studygroup.common

data class CursorResponse<T>(
    // 실제 데이터 리스트
    val content: List<T>,
    // 다음 요청 때 보낼 커서 ID (null이면 마지막 페이지)
    val nextCursorId: Long?,
    // 다음 데이터가 있는지 여부
    val hasNext: Boolean,
)
