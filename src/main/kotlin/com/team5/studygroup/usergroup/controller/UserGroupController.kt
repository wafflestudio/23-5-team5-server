package com.team5.studygroup.usergroup.controller

import com.team5.studygroup.user.LoggedInUser
import com.team5.studygroup.usergroup.dto.JoinGroupDto
import com.team5.studygroup.usergroup.dto.WithdrawGroupDto
import com.team5.studygroup.usergroup.service.UserGroupService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/groups/join")
class UserGroupController(
    private val userGroupService: UserGroupService,
) {
    @PostMapping("")
    fun joinGroup(
        @RequestBody joinGroupDto: JoinGroupDto,
        @LoggedInUser requestingUserId: Long,
    ): ResponseEntity<Unit> {
        return ResponseEntity.ok(userGroupService.joinGroup(joinGroupDto, requestingUserId))
    }

    @DeleteMapping("")
    fun withdrawGroup(
        @RequestBody withdrawGroupDto: WithdrawGroupDto,
        @LoggedInUser requestingUserId: Long,
    ): ResponseEntity<Unit> {
        return ResponseEntity.ok(userGroupService.withdrawGroup(withdrawGroupDto, requestingUserId))
    }
}
