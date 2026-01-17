package com.team5.studygroup.user.model

enum class Role(val key: String, val description: String) {
    USER("ROLE_USER", "일반 사용자"),
    ADMIN("ROLE_ADMIN", "관리자"),
}
