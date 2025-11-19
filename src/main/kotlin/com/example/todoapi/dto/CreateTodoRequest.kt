package com.example.todoapi.dto

import com.example.todoapi.entity.Priority
import com.example.todoapi.models.TodoModel
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class CreateTodoRequest(
    @field:NotBlank(message = "Title must not be empty!")
    @field:Size(min = 1, max = 100, message = "Title size should be between 1 and 100")
    val title: String,
    @field:Size(max = 500, message = "Description max size should be 500")
    val description: String? = null,
    val completed: Boolean? = null,
    val priority: Priority? = null,
)

fun CreateTodoRequest.toModel(): TodoModel =
    TodoModel(
        title = this.title,
        description = this.description,
        completed = this.completed ?: false,
        priority = this.priority ?: Priority.MEDIUM,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now(),
    )
