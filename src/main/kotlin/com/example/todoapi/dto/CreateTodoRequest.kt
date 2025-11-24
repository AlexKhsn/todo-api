package com.example.todoapi.dto

import com.example.todoapi.entity.Priority
import com.example.todoapi.models.TodoModel
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class CreateTodoRequest(
    @field:Schema(description = "Task title", example = "Test")
    @field:NotBlank(message = "Title must not be empty!")
    @field:Size(min = 1, max = 100, message = "Title size should be between 1 and 100")
    val title: String,
    @field:Schema(description = "Task description", example = "Test description")
    @field:Size(max = 500, message = "Description max size should be 500")
    val description: String? = null,
    @field:Schema(description = "Task status", example = "true")
    val completed: Boolean? = null,
    @field:Schema(description = "Task priority", example = "MEDIUM")
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
