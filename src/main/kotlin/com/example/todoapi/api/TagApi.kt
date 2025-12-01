package com.example.todoapi.api

import com.example.todoapi.dto.CreateTagRequest
import com.example.todoapi.dto.TagResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@Tag(name = "Tag API", description = "API для управления тэгами")
@RequestMapping("/api/tags")
interface TagApi {
    @Operation(
        summary = "Create a new tag",
        description = "Create a new tag",
    )
    @PostMapping
    fun createTag(
        @RequestBody request: CreateTagRequest,
    ): ResponseEntity<TagResponse>

    @Operation(
        summary = "Get all tags",
        description = "Get all existing tags",
    )
    @GetMapping
    fun getAllTags(): ResponseEntity<List<TagResponse>>

    @Operation(
        summary = "Get tag by ID",
        description = "Get an existing tag by ID",
    )
    @GetMapping("/{id}")
    fun getTagById(
        @PathVariable id: Long,
    ): ResponseEntity<TagResponse>

    @Operation(
        summary = "Delete tag by ID",
        description = "Delete an existing tag by ID",
    )
    @DeleteMapping("/{id}")
    fun deleteTag(
        @PathVariable id: Long,
    ): ResponseEntity<TagResponse>
}
