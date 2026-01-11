package com.team5.studygroup.group.controller

import com.team5.studygroup.group.dto.CreateGroupDto
import com.team5.studygroup.group.dto.DeleteGroupDto
import com.team5.studygroup.group.dto.ExpireGroupDto
import com.team5.studygroup.group.service.GroupService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/groups")
class GroupController(
    private val groupService: GroupService
) {
    @PostMapping("")
    fun createGroup(
        @RequestBody createGroupDto: CreateGroupDto,
    ): String {
        return groupService.createGroup(createGroupDto)
    }

    @DeleteMapping("")
    fun deleteGroup(
        @RequestBody deleteGroupDto: DeleteGroupDto,
    ): String {
        return groupService.deleteGroup(deleteGroupDto)
    }

    @PatchMapping("/expire")
    fun expireGroup(
        @RequestBody expireGroupDto: ExpireGroupDto,
    ): String {
        return groupService.expireGroup(expireGroupDto)
    }

}