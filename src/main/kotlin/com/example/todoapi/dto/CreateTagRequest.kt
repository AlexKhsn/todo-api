package com.example.todoapi.dto

import com.example.todoapi.models.TagModel
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

data class CreateTagRequest(
    @field:Schema(description = "Tag name", example = "Test")
    @field:NotBlank(message = "Tag name cannot be blank")
    val name: String,
)

fun CreateTagRequest.toModel(): TagModel =
    TagModel(
        name = this.name,
        createdAt = LocalDateTime.now(),
    )
