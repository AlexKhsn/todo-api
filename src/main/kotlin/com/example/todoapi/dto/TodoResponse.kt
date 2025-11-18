package com.example.todoapi.dto

import com.example.todoapi.entity.Priority
import com.example.todoapi.entity.Todo
import java.time.LocalDateTime

data class TodoResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val completed: Boolean,
    val priority: Priority,
    val createdAt: LocalDateTime,
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
