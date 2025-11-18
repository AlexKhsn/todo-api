package com.example.todoapi.models

import com.example.todoapi.entity.Priority
import com.example.todoapi.entity.Todo
import java.time.LocalDateTime

data class TodoModel(
    val id: Long? = null,
    val title: String,
    val description: String? = null,
    val completed: Boolean,
    val priority: Priority,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

fun TodoModel.toEntity() : Todo = Todo(
    id = this.id,
    title = this.title,
    description = this.description,
    completed = this.completed,
    priority = this.priority,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
)
