package com.team5.studygroup.group.controller

import com.team5.studygroup.common.ErrorResponse
import com.team5.studygroup.group.dto.CreateGroupDto
import com.team5.studygroup.group.dto.DeleteGroupDto
import com.team5.studygroup.group.dto.ExpireGroupDto
import com.team5.studygroup.group.dto.GroupSearchResponse
import com.team5.studygroup.group.service.GroupService
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
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Group API", description = "스터디 그룹 관련 API")
@RestController
@RequestMapping("/groups")
class GroupController(
    private val groupService: GroupService,
) {
    @Operation(
        summary = "그룹 생성",
        description = "새로운 스터디 그룹을 생성합니다. (헤더의 토큰을 통해 그룹장을 식별합니다)",
        security = [SecurityRequirement(name = "Authorization")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "생성 성공",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = GroupSearchResponse::class),
                        examples = [
                            ExampleObject(
                                name = "그룹 생성 성공 예시",
                                summary = "새로 생성된 그룹 정보 반환 (리더 정보 포함)",
                                value = """
                                    {
                                      "id": 155,
                                      "groupName": "스프링 부트 입문 스터디",
                                      "description": "스프링 부트 3.x 버전을 처음부터 차근차근 배웁니다.",
                                      "categoryId": 1,
                                      "subCategoryId": 12,
                                      "capacity": 4,
                                      "leaderId": 501,
                                      "leaderNickname": "개발왕",
                                      "leaderBio": "서버 개발자를 꿈꾸는 취준생입니다.",
                                      "leaderUserName": "waffle@snu.ac.kr",
                                      "leaderProfileImageUrl": "https://s3.ap-northeast-2.amazonaws.com/team5-bucket/profiles/default_user.jpg",
                                      "isOnline": true,
                                      "location": "온라인 (Zoom)",
                                      "status": "RECRUITING",
                                      "createdAt": "2026-02-05T09:30:00Z"
                                    }
                                """,
                            ),
                        ],
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청 (입력값 검증 실패)",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "입력값 오류",
                                summary = "필수 필드 누락 등",
                                value = """{"errorCode": 9001, "message": "그룹 이름은 필수입니다.", "timestamp": "2026-02-03T10:00:00"}""",
                            ),
                        ],
                    ),
                ],
            ),
        ],
    )
    @PostMapping("")
    fun createGroup(
        @Valid @RequestBody createGroupDto: CreateGroupDto,
        @Parameter(hidden = true) @LoggedInUser requestingUserId: Long,
    ): ResponseEntity<GroupSearchResponse> {
        val response = groupService.createGroup(createGroupDto, requestingUserId)

        return ResponseEntity
            .status(201)
            .body(response)
    }

    @Operation(
        summary = "그룹 삭제",
        description = "기존 스터디 그룹을 삭제합니다. 그룹장만 삭제 가능합니다.",
        security = [SecurityRequirement(name = "Authorization")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "삭제 성공",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        examples = [ExampleObject(value = """{"deletedId": 1}""")],
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "403",
                description = "권한 없음",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))],
            ),
            ApiResponse(
                responseCode = "404",
                description = "그룹 없음",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))],
            ),
        ],
    )
    @DeleteMapping("")
    fun deleteGroup(
        @RequestBody deleteGroupDto: DeleteGroupDto,
        @Parameter(hidden = true) @LoggedInUser requestingUserId: Long,
    ): ResponseEntity<Map<String, Long>> {
        groupService.deleteGroup(deleteGroupDto, requestingUserId)
        return ResponseEntity.ok(mapOf("deletedId" to deleteGroupDto.groupId))
    }

    @Operation(
        summary = "그룹 조기 만료/종료",
        description = "스터디 그룹을 종료(만료)시킵니다.",
        security = [SecurityRequirement(name = "Authorization")],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "만료 처리 성공",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        examples = [ExampleObject(value = """{"expiredId": 1}""")],
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "403",
                description = "권한 없음",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))],
            ),
            ApiResponse(
                responseCode = "409",
                description = "이미 만료됨",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))],
            ),
        ],
    )
    @PatchMapping("/expire")
    fun expireGroup(
        @RequestBody expireGroupDto: ExpireGroupDto,
        @Parameter(hidden = true) @LoggedInUser requestingUserId: Long,
    ): ResponseEntity<Map<String, Long>> {
        groupService.expireGroup(expireGroupDto, requestingUserId)
        return ResponseEntity.ok(mapOf("expiredId" to expireGroupDto.groupId))
    }
}
