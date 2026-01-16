package com.team5.studygroup.usergroup.controller

import com.team5.studygroup.usergroup.dto.JoinGroupDto
import com.team5.studygroup.usergroup.dto.WithdrawGroupDto
import com.team5.studygroup.usergroup.service.UserGroupService
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
    ): String {
        return userGroupService.joinGroup(joinGroupDto)
    }

    @DeleteMapping("")
    fun withdrawGroup(
        @RequestBody withdrawGroupDto: WithdrawGroupDto,
    ): String {
        return userGroupService.withdrawGroup(withdrawGroupDto)
    }
}
