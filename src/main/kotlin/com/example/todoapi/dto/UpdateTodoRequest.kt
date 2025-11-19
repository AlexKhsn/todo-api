package com.example.todoapi.dto

import com.example.todoapi.entity.Priority
import jakarta.validation.constraints.Size

data class UpdateTodoRequest(
    @field:Size(min = 1, max = 100, message = "Title size should be between 1 and 100")
    val title: String? = null,
    @field:Size(max = 500, message = "Description max size should be 500")
    val description: String? = null,
    val completed: Boolean? = null,
    val priority: Priority? = null,
)
