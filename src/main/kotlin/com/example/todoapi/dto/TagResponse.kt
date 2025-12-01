package com.example.todoapi.dto

import com.example.todoapi.models.TagModel
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

data class TagResponse(
    @field:Schema(description = "Tag ID", example = "1")
    val id: Long,
    @field:Schema(description = "Tag name", example = "Test")
    val name: String,
    @field:Schema(description = "Creating date time", example = "2025-11-24T15:33:02.951356")
    val createdAt: LocalDateTime,
)

fun TagModel.toResponse() =
    TagResponse(
        id = this.id!!,
        name = this.name,
        createdAt = this.createdAt,
    )
