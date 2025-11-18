package com.example.todoapi.models

import com.example.todoapi.entity.Priority
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
