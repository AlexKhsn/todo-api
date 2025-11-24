package com.example.todoapi.dto

import com.example.todoapi.entity.Priority
import com.example.todoapi.entity.Todo
import com.example.todoapi.models.TodoModel
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

data class TodoResponse(
    @field:Schema(description = "Task ID", example = "1")
    val id: Long,
    @field:Schema(description = "Task title", example = "Test task")
    val title: String,
    @field:Schema(description = "Task description", example = "Test description")
    val description: String?,
    @field:Schema(description = "Task status", example = "true")
    val completed: Boolean,
    @field:Schema(description = "Task priority", example = "MEDIUM")
    val priority: Priority,
    @field:Schema(description = "Creating date time", example = "2025-11-24T15:33:02.951356")
    val createdAt: LocalDateTime,
    @field:Schema(description = "Updating date time", example = "2025-11-24T15:33:02.951356")
    val updatedAt: LocalDateTime,
)

fun Todo.toResponse(): TodoResponse =
    TodoResponse(
        id = this.id!!,
        title = this.title,
        description = this.description,
        completed = this.completed,
        priority = this.priority,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )

fun TodoModel.toResponse(): TodoResponse =
    TodoResponse(
        id = this.id!!,
        title = this.title,
        description = this.description,
        completed = this.completed,
        priority = this.priority,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )
