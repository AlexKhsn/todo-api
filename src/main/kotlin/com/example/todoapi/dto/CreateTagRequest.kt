package com.example.todoapi.dto

import com.example.todoapi.models.TagModel
import java.time.LocalDateTime

data class CreateTagRequest(
    val name: String,
)

fun CreateTagRequest.toModel(): TagModel =
    TagModel(
        name = this.name,
        createdAt = LocalDateTime.now(),
    )
