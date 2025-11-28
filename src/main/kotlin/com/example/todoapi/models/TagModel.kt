package com.example.todoapi.models

import com.example.todoapi.entity.Tag
import java.time.LocalDateTime

data class TagModel(
    val id: Long? = null,
    val name: String,
    val createdAt: LocalDateTime,
)

fun TagModel.toEntity() =
    Tag(
        id = this.id,
        name = this.name,
        createdAt = this.createdAt,
    )

fun Tag.toModel() =
    TagModel(
        id = this.id,
        name = this.name,
        createdAt = this.createdAt,
    )
