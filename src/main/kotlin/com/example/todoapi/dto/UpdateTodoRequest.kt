package com.example.todoapi.dto

import com.example.todoapi.entity.Priority
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size

data class UpdateTodoRequest(
    @field:Schema(description = "Task title", example = "Test task")
    @field:Size(min = 1, max = 100, message = "Title size should be between 1 and 100")
    val title: String? = null,
    @field:Schema(description = "Task description", example = "Test description")
    @field:Size(max = 500, message = "Description max size should be 500")
    val description: String? = null,
    @field:Schema(description = "Task status", example = "true")
    val completed: Boolean? = null,
    @field:Schema(description = "Task priority", example = "MEDIUM")
    val priority: Priority? = null,
)
