package com.team5.studygroup.user.controller

import com.team5.studygroup.common.CursorResponse
import com.team5.studygroup.common.ErrorResponse
import com.team5.studygroup.user.LoggedInUser
import com.team5.studygroup.user.dto.UserSearchResponseDto
import com.team5.studygroup.user.service.UserSearchService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "User Search API", description = "유저 검색 및 조회 (그룹장 전용)")
@RestController
@RequestMapping("/users/search")
@Validated
class UserSearchController(
    private val userSearchService: UserSearchService,
) {
    @Operation(
        summary = "그룹 내 유저 목록 조회",
        description = "특정 그룹에 가입된 유저 목록을 조회합니다. 그룹장만 접근 가능하며, 커서 기반 페이징을 지원합니다.",
        security = [SecurityRequirement(name = "Authorization")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = CursorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "성공 예시",
                                value = """
                                    {
                                      "content": [
                                        {
                                          "id": 15,
                                          "username": "user_kim",
                                          "major": "컴퓨터공학과",
                                          "nickname": "코딩하는_김철수"
                                        },
                                        {
                                          "id": 12,
                                          "username": "user_lee",
                                          "major": "시각디자인학과",
                                          "nickname": "디자인_이영희"
                                        }
                                      ],
                                      "nextCursorId": 12,
                                      "hasNext": true
                                    }
                                """,
                            ),
                            ExampleObject(
                                name = "결과 없음(빈 리스트)",
                                value = """
                                    {
                                      "content": [],
                                      "nextCursorId": null,
                                      "hasNext": false
                                    }
                                """,
                            ),
                        ],
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "403",
                description = "권한 없음",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "권한 없음 (1006)",
                                summary = "그룹장이 아닌 유저가 접근 시",
                                value = """
                                    {"errorCode": 1006, 
                                    "message": "그룹장만 그룹 참여 인원을 확인할 수 있습니다.", 
                                    "timestamp": "2026-02-03T22:00:00"}""",
                            ),
                        ],
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "404",
                description = "대상 없음",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "그룹 없음 (3001)",
                                value = """
                                    {"errorCode": 3001, 
                                    "message": "해당 그룹을 찾을 수 없습니다.", 
                                    "timestamp": "2026-02-03T22:00:00"}""",
                            ),
                        ],
                    ),
                ],
            ),
        ],
    )
    @GetMapping("")
    fun searchUsersInGroup(
        @Parameter(description = "조회할 그룹의 ID", example = "1", required = true) @RequestParam groupId: Long,
        @Parameter(hidden = true) @LoggedInUser requestingUserId: Long,
        @Parameter(description = "커서 ID (이전 페이지의 마지막 유저 ID)") @RequestParam(required = false) cursorId: Long?,
        @Parameter(description = "페이지 사이즈 (1~50), 기본값은 10입니다.") @RequestParam(defaultValue = "10") @Min(1) @Max(50) size: Int,
    ): ResponseEntity<CursorResponse<UserSearchResponseDto>> {
        val result = userSearchService.searchUsersInGroup(groupId, requestingUserId, cursorId, size)
        return ResponseEntity.ok(result)
    }
}
