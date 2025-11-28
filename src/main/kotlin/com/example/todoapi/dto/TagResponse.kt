package com.example.todoapi.dto

import com.example.todoapi.models.TagModel
import java.time.LocalDateTime

data class TagResponse(
    val id: Long,
    val name: String,
    val createdAt: LocalDateTime,
)

fun TagModel.toResponse() =
    TagResponse(
        id = this.id!!,
        name = this.name,
        createdAt = this.createdAt,
    )
