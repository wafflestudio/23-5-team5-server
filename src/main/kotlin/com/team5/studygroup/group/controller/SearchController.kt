package com.team5.studygroup.group.controller

import com.team5.studygroup.common.CursorResponse
import com.team5.studygroup.common.ErrorResponse
import com.team5.studygroup.group.dto.GroupResponse
import com.team5.studygroup.group.service.SearchService
import com.team5.studygroup.user.LoggedInUser
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

@Tag(name = "Group Search API", description = "스터디 그룹 검색 (무한 스크롤 방식)")
@RestController
@RequestMapping("/groups/search")
@Validated
class SearchController(
    private val searchService: SearchService,
) {
    @Operation(summary = "그룹 검색", description = "조건에 맞는 그룹을 검색합니다. 첫 조회 시 cursorId는 null로 입력해주세요.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "검색 성공"),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 (페이지 크기 범위 오류)",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "Size 범위 초과 (9002)",
                                summary = "size가 50을 초과한 경우",
                                value = """{"errorCode": 9002, "message": "size는 50 이하여야 합니다.", "timestamp": "2026-02-03T22:00:00"}""",
                            ),
                            ExampleObject(
                                name = "Size 범위 미만 (9002)",
                                summary = "size가 1 미만인 경우",
                                value = """{"errorCode": 9002, "message": "size는 1 이상이어야 합니다.", "timestamp": "2026-02-03T22:00:00"}""",
                            ),
                        ],
                    ),
                ],
            ),
        ],
    )
    @GetMapping("")
    fun search(
        @RequestParam(required = false) categoryId: Long?,
        @RequestParam(required = false) subCategoryId: Long?,
        @RequestParam(required = false) keyword: String?,
        @Parameter(description = "이전 페이지의 마지막 아이템 ID") @RequestParam(required = false) cursorId: Long?,
        @Parameter(description = "한 번에 가져올 개수 (1~50), 기본값은 10입니다") @RequestParam(defaultValue = "10") @Min(1) @Max(50) size: Int,
    ): ResponseEntity<CursorResponse<GroupResponse>> {
        val result = searchService.search(categoryId, subCategoryId, keyword, cursorId, size)
        return ResponseEntity.ok(result)
    }

    @Operation(summary = "내가 만든 그룹", description = "로그인한 유저가 생성한 그룹을 조회합니다.", security = [SecurityRequirement(name = "Authorization")])
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "조회 성공"),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 (페이지 크기 범위 오류)",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "Size 범위 초과 (9002)",
                                value = """{"errorCode": 9002, "message": "size는 50 이하여야 합니다.", "timestamp": "2026-02-03T22:00:00"}""",
                            ),
                        ],
                    ),
                ],
            ),
        ],
    )
    @GetMapping("/me")
    fun searchMyGroup(
        @Parameter(hidden = true) @LoggedInUser requestingUserId: Long,
        @RequestParam(required = false) cursorId: Long?,
        @Parameter(description = "한 번에 가져올 개수 (1~50), 기본값은 10입니다") @RequestParam(defaultValue = "10") @Min(1) @Max(50) size: Int,
    ): ResponseEntity<CursorResponse<GroupResponse>> {
        val result = searchService.searchMyGroup(requestingUserId, cursorId, size)
        return ResponseEntity.ok(result)
    }

    @Operation(summary = "내가 참여한 그룹", description = "로그인한 유저가 참여 중인 그룹을 조회합니다.", security = [SecurityRequirement(name = "Authorization")])
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "조회 성공"),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 (페이지 크기 범위 오류)",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "Size 범위 초과 (9002)",
                                value = """{"errorCode": 9002, "message": "size는 50 이하여야 합니다.", "timestamp": "2026-02-03T22:00:00"}""",
                            ),
                        ],
                    ),
                ],
            ),
        ],
    )
    @GetMapping("/joined")
    fun searchJoinedGroup(
        @Parameter(hidden = true) @LoggedInUser requestingUserId: Long,
        @RequestParam(required = false) cursorId: Long?,
        @Parameter(description = "한 번에 가져올 개수 (1~50), 기본값은 10입니다") @RequestParam(defaultValue = "10") @Min(1) @Max(50) size: Int,
    ): ResponseEntity<CursorResponse<GroupResponse>> {
        val result = searchService.searchJoinedGroup(requestingUserId, cursorId, size)
        return ResponseEntity.ok(result)
    }
}
