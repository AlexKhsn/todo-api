package com.example.todoapi.dto

import com.example.todoapi.entity.Priority
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateTodoRequest(
    @field:NotBlank
    @field:Size(min = 1, max = 100)
    val title: String,
    @field:Size(max = 500)
    val description: String? = null,
    val completed: Boolean? = null,
    val priority: Priority? = null,
)
