package com.example.todoapi.controllers

import com.example.todoapi.dto.CreateTodoRequest
import com.example.todoapi.dto.TodoResponse
import com.example.todoapi.dto.toResponse
import com.example.todoapi.service.TodoService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/todos")
class TodoController(
    private val todoService: TodoService,
) {
    @PostMapping
    fun createNewToDo(
        @RequestBody
        @Valid
        request: CreateTodoRequest,
    ): ResponseEntity<TodoResponse> {
        val createdModel = todoService.createTodo(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdModel.toResponse())
    }

    @GetMapping()
    fun getAllTodos(): ResponseEntity<List<TodoResponse>> {
        val foundModels = todoService.getAllTodos()
        return ResponseEntity.status(HttpStatus.OK).body(foundModels.map { it.toResponse() })
    }

    @GetMapping("/{id}")
    fun getTodoById(
        @PathVariable
        id: Long,
    ): ResponseEntity<TodoResponse> {
        val foundModel = todoService.getTodoById(id)
        return ResponseEntity.status(HttpStatus.OK).body(foundModel.toResponse())
    }
}
