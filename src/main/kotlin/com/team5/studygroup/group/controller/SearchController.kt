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
            ApiResponse(
                responseCode = "200",
                description = "검색 성공",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = CursorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "스터디 검색 결과 예시",
                                summary = "스터디 데이터 목록",
                                value = """
                    {
                      "content": [
                        {
                          "id": 125,
                          "groupName": "백엔드 스터디",
                          "description": "실무 위주로 클린 아키텍처와 TDD를 공부합니다. 매주 일요일 강남역 모임.",
                          "categoryId": 1,
                          "subCategoryId": 11,
                          "capacity": 6,
                          "leaderId": 501,
                          "isOnline": false,
                          "location": "서울 강남역 인근 카페",
                          "status": "RECRUITING",
                          "createdAt": "2026-02-01T10:00:00Z"
                        },
                        {
                          "id": 122,
                          "groupName": "오픽(OPIc) AL 목표 매일 스피킹",
                          "description": "매일 밤 10시 디스코드로 1시간 동안 프리토킹 하실 분들 모십니다.",
                          "categoryId": 2,
                          "subCategoryId": 24,
                          "capacity": 4,
                          "leaderId": 612,
                          "isOnline": true,
                          "location": "온라인 (Discord)",
                          "status": "RECRUITING",
                          "createdAt": "2026-01-30T15:20:00Z"
                        },
                        {
                          "id": 119,
                          "groupName": "부동산 경매 기초부터 실전까지",
                          "categoryName": "재테크/금융",
                          "description": "실제 경매 물건 같이 분석해보고 모의 입찰 해보는 스터디입니다.",
                          "categoryId": 3,
                          "subCategoryId": 35,
                          "capacity": 10,
                          "leaderId": 44,
                          "isOnline": false,
                          "location": "경기도 성남시 분당구",
                          "status": "EXPIRED",
                          "createdAt": "2026-01-25T09:00:00Z"
                        }
                      ],
                      "nextCursorId": 119,
                      "hasNext": true
                    }
                    """,
                            ),
                        ],
                    ),
                ],
            ),
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
        @Parameter(description = "이전 페이지의 마지막 아이템 ID, 첫 조회 시 cursorId는 null로 입력해주세요.") @RequestParam(required = false) cursorId: Long?,
        @Parameter(description = "한 번에 가져올 개수 (1~50), 기본값은 10입니다") @RequestParam(defaultValue = "10") @Min(1) @Max(50) size: Int,
    ): ResponseEntity<CursorResponse<GroupResponse>> {
        val result = searchService.search(categoryId, subCategoryId, keyword, cursorId, size)
        return ResponseEntity.ok(result)
    }

    @Operation(summary = "내가 만든 그룹", description = "로그인한 유저가 생성한 그룹을 조회합니다.", security = [SecurityRequirement(name = "Authorization")])
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
                                name = "내가 생성한 그룹 목록 예시",
                                summary = "로그인한 유저(leaderId: 501)가 방장인 그룹들",
                                value = """
                    {
                      "content": [
                        {
                          "id": 150,
                          "groupName": "코틀린 마스터리: 코루틴 깊게 파기",
                          "description": "비동기 프로그래밍의 핵심, 코루틴을 공식 문서 위주로 함께 공부합니다.",
                          "categoryId": 1,
                          "subCategoryId": 11,
                          "capacity": 5,
                          "leaderId": 501,
                          "isOnline": true,
                          "location": "온라인 (Google Meet)",
                          "status": "RECRUITING",
                          "createdAt": "2026-02-01T14:00:00Z"
                        },
                        {
                          "id": 142,
                          "groupName": "매일 1시간 알고리즘 문제 풀이",
                          "description": "코딩 테스트 대비를 위해 매일 꾸준히 리트코드 한 문제씩 풉니다.",
                          "categoryId": 1,
                          "subCategoryId": 15,
                          "capacity": 8,
                          "leaderId": 501,
                          "isOnline": true,
                          "location": "온라인 (Slack/Github)",
                          "status": "FULL",
                          "createdAt": "2026-01-20T09:00:00Z"
                        }
                      ],
                      "nextCursorId": 142,
                      "hasNext": false
                    }
                    """,
                            ),
                        ],
                    ),
                ],
            ),
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
        @Parameter(description = "이전 페이지의 마지막 아이템 ID, 첫 조회 시 cursorId는 null로 입력해주세요.") @RequestParam(required = false) cursorId: Long?,
        @Parameter(description = "한 번에 가져올 개수 (1~50), 기본값은 10입니다") @RequestParam(defaultValue = "10") @Min(1) @Max(50) size: Int,
    ): ResponseEntity<CursorResponse<GroupResponse>> {
        val result = searchService.searchMyGroup(requestingUserId, cursorId, size)
        return ResponseEntity.ok(result)
    }

    @Operation(summary = "내가 참여한 그룹", description = "로그인한 유저가 참여 중인 그룹을 조회합니다.", security = [SecurityRequirement(name = "Authorization")])
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "검색 성공",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = CursorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "스터디 검색 결과 예시",
                                summary = "스터디 데이터 목록",
                                value = """
                    {
                      "content": [
                        {
                          "id": 125,
                          "groupName": "백엔드 스터디",
                          "description": "실무 위주로 클린 아키텍처와 TDD를 공부합니다. 매주 일요일 강남역 모임.",
                          "categoryId": 1,
                          "subCategoryId": 11,
                          "capacity": 6,
                          "leaderId": 501,
                          "isOnline": false,
                          "location": "서울 강남역 인근 카페",
                          "status": "RECRUITING",
                          "createdAt": "2026-02-01T10:00:00Z"
                        },
                        {
                          "id": 122,
                          "groupName": "오픽(OPIc) AL 목표 매일 스피킹",
                          "description": "매일 밤 10시 디스코드로 1시간 동안 프리토킹 하실 분들 모십니다.",
                          "categoryId": 2,
                          "subCategoryId": 24,
                          "capacity": 4,
                          "leaderId": 612,
                          "isOnline": true,
                          "location": "온라인 (Discord)",
                          "status": "RECRUITING",
                          "createdAt": "2026-01-30T15:20:00Z"
                        },
                        {
                          "id": 119,
                          "groupName": "부동산 경매 기초부터 실전까지",
                          "categoryName": "재테크/금융",
                          "description": "실제 경매 물건 같이 분석해보고 모의 입찰 해보는 스터디입니다.",
                          "categoryId": 3,
                          "subCategoryId": 35,
                          "capacity": 10,
                          "leaderId": 44,
                          "isOnline": false,
                          "location": "경기도 성남시 분당구",
                          "status": "EXPIRED",
                          "createdAt": "2026-01-25T09:00:00Z"
                        }
                      ],
                      "nextCursorId": 119,
                      "hasNext": true
                    }
                    """,
                            ),
                        ],
                    ),
                ],
            ),
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
