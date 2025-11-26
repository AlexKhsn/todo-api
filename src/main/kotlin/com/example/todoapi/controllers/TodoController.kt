package com.example.todoapi.controllers

import com.example.todoapi.api.TodoApi
import com.example.todoapi.dto.CreateTodoRequest
import com.example.todoapi.dto.TodoResponse
import com.example.todoapi.dto.UpdateTodoRequest
import com.example.todoapi.dto.toResponse
import com.example.todoapi.entity.Priority
import com.example.todoapi.service.TodoService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class TodoController(
    private val todoService: TodoService,
) : TodoApi {
    override fun createNewToDo(request: CreateTodoRequest): ResponseEntity<TodoResponse> {
        val createdModel = todoService.createTodo(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdModel.toResponse())
    }

    override fun getAllTodos(
        completed: Boolean?,
        subtitle: String?,
        priority: Priority?,
        pageable: Pageable,
    ): ResponseEntity<Page<TodoResponse>> {
        val foundModels = todoService.getTodos(completed, subtitle, priority, pageable)
        return ResponseEntity.status(HttpStatus.OK).body(foundModels.map { it.toResponse() })
    }

    override fun getTodoById(id: Long): ResponseEntity<TodoResponse> {
        val foundModel = todoService.getTodoById(id)
        return ResponseEntity.status(HttpStatus.OK).body(foundModel.toResponse())
    }

    override fun updateTodoById(
        id: Long,
        updateRequest: UpdateTodoRequest,
    ): ResponseEntity<TodoResponse> {
        val updatedModel = todoService.updateTodo(id, updateRequest)
        return ResponseEntity.status(HttpStatus.OK).body(updatedModel.toResponse())
    }

    override fun toggleTodoCompleted(id: Long): ResponseEntity<TodoResponse> {
        val toggledModel = todoService.toggleComplete(id)
        return ResponseEntity.status(HttpStatus.OK).body(toggledModel.toResponse())
    }

    override fun deleteTodoById(id: Long): ResponseEntity<TodoResponse> {
        val deletedModel = todoService.deleteTodo(id)
        return ResponseEntity.status(HttpStatus.OK).body(deletedModel.toResponse())
    }
}
