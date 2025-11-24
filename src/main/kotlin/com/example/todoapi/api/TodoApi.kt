package com.example.todoapi.api

import com.example.todoapi.dto.CreateTodoRequest
import com.example.todoapi.dto.TodoResponse
import com.example.todoapi.dto.UpdateTodoRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Tag(name = "Todo API", description = "API для управления задачами")
@RequestMapping("/api/todos")
interface TodoApi {
    @Operation(
        summary = "Create a new task",
        description = "Create a new task with entered/default data",
    )
    @PostMapping
    fun createNewToDo(
        @RequestBody
        @Valid
        request: CreateTodoRequest,
    ): ResponseEntity<TodoResponse>

    @Operation(
        summary = "Get all existing tasks",
        description = "Get all existing tasks (filtered / non-filtered)",
    )
    @GetMapping
    fun getAllTodos(
        @RequestParam(required = false)
        completed: Boolean? = null,
        @RequestParam(required = false)
        subtitle: String? = null,
    ): ResponseEntity<List<TodoResponse>>

    @Operation(
        summary = "Get task by ID",
        description = "Get existing task by ID or Exception",
    )
    @GetMapping("/{id}")
    fun getTodoById(
        @PathVariable
        id: Long,
    ): ResponseEntity<TodoResponse>

    @Operation(
        summary = "Update task by ID",
        description = "Update existing task and get it back or Exception",
    )
    @PutMapping("/{id}")
    fun updateTodoById(
        @PathVariable
        id: Long,
        @RequestBody
        @Valid
        updateRequest: UpdateTodoRequest,
    ): ResponseEntity<TodoResponse>

    @Operation(
        summary = "Change task status by ID",
        description = "Change task status (completed / non-completed) by ID",
    )
    @PatchMapping("/{id}/toggle")
    fun toggleTodoCompleted(
        @PathVariable
        id: Long,
    ): ResponseEntity<TodoResponse>

    @Operation(
        summary = "Delete task by ID",
        description = "Delete existing task by ID and get it back or Exception",
    )
    @DeleteMapping("/{id}")
    fun deleteTodoById(
        @PathVariable
        id: Long,
    ): ResponseEntity<TodoResponse>
}
