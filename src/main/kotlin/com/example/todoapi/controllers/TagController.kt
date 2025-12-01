package com.example.todoapi.controllers

import com.example.todoapi.api.TagApi
import com.example.todoapi.dto.CreateTagRequest
import com.example.todoapi.dto.TagResponse
import com.example.todoapi.dto.toResponse
import com.example.todoapi.service.TagService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class TagController(
    private val tagService: TagService,
) : TagApi {
    override fun createTag(request: CreateTagRequest): ResponseEntity<TagResponse> {
        val tagModel = tagService.createTag(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(tagModel.toResponse())
    }

    override fun getAllTags(): ResponseEntity<List<TagResponse>> {
        val tagsModels = tagService.getAllTags()
        return ResponseEntity.status(HttpStatus.OK).body(tagsModels.map { it.toResponse() })
    }

    override fun getTagById(id: Long): ResponseEntity<TagResponse> {
        val tagModel = tagService.getTagById(id)
        return ResponseEntity.status(HttpStatus.OK).body(tagModel.toResponse())
    }

    override fun deleteTag(id: Long): ResponseEntity<TagResponse> {
        val tagModel = tagService.deleteTagById(id)
        return ResponseEntity.status(HttpStatus.OK).body(tagModel.toResponse())
    }
}
