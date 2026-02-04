package com.team5.studygroup.usergroup.controller

import com.team5.studygroup.common.ErrorResponse
import com.team5.studygroup.user.LoggedInUser
import com.team5.studygroup.usergroup.dto.JoinGroupDto
import com.team5.studygroup.usergroup.dto.WithdrawGroupDto
import com.team5.studygroup.usergroup.service.UserGroupService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Group Join/Withdraw API", description = "스터디 그룹 가입 및 탈퇴 관리")
@RestController
@RequestMapping("/groups/join")
class UserGroupController(
    private val userGroupService: UserGroupService,
) {
    @Operation(
        summary = "그룹 가입",
        description = "스터디 그룹에 가입 신청을 합니다. 정원 초과나 모집 마감 시 가입이 불가능합니다.",
        security = [SecurityRequirement(name = "Authorization")],
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "가입 성공"),
            ApiResponse(
                responseCode = "400",
                description = "가입 불가 상황",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "모집 마감 (3005)",
                                summary = "그룹 상태가 EXPIRED인 경우",
                                value = """
                                    {"errorCode": 3005, 
                                    "message": "현재 모집 중인 그룹이 아닙니다.", 
                                    "timestamp": "2026-02-03T22:00:00"}""",
                            ),
                            ExampleObject(
                                name = "정원 초과 (3006)",
                                summary = "가입 인원이 capacity에 도달한 경우",
                                value = """
                                    {"errorCode": 3006, 
                                    "message": "그룹의 정원이 가득 차서 가입할 수 없습니다.", 
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
            ApiResponse(
                responseCode = "409",
                description = "중복 가입",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "이미 가입됨 (3002)",
                                value = """
                                    {"errorCode": 3002, 
                                    "message": "이미 가입한 그룹입니다.", 
                                    "timestamp": "2026-02-03T22:00:00"}""",
                            ),
                        ],
                    ),
                ],
            ),
        ],
    )
    @PostMapping("")
    fun joinGroup(
        @RequestBody joinGroupDto: JoinGroupDto,
        @Parameter(hidden = true) @LoggedInUser requestingUserId: Long,
    ): ResponseEntity<Unit> {
        userGroupService.joinGroup(joinGroupDto, requestingUserId)
        return ResponseEntity.ok().build()
    }

    @Operation(
        summary = "그룹 탈퇴",
        description = "참여 중인 스터디 그룹에서 탈퇴합니다.",
        security = [SecurityRequirement(name = "Authorization")],
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "탈퇴 성공"),
            ApiResponse(
                responseCode = "404",
                description = "대상 없음",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "가입 정보 없음 (3003)",
                                value = """
                                    {"errorCode": 3003, 
                                    "message": "가입한 그룹이 아닙니다.", 
                                    "timestamp": "2026-02-03T22:00:00"}""",
                            ),
                        ],
                    ),
                ],
            ),
        ],
    )
    @DeleteMapping("")
    fun withdrawGroup(
        @RequestBody withdrawGroupDto: WithdrawGroupDto,
        @Parameter(hidden = true) @LoggedInUser requestingUserId: Long,
    ): ResponseEntity<Unit> {
        userGroupService.withdrawGroup(withdrawGroupDto, requestingUserId)
        return ResponseEntity.ok().build()
    }
}
