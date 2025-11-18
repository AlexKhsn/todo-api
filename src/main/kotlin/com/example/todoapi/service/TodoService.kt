package com.example.todoapi.service

import com.example.todoapi.dto.CreateTodoRequest
import com.example.todoapi.dto.TodoResponse
import com.example.todoapi.dto.toModel
import com.example.todoapi.dto.toResponse
import com.example.todoapi.models.toEntity
import com.example.todoapi.repository.TodoRepository
import org.springframework.stereotype.Service

@Service
class TodoService(
    private val todoRepository: TodoRepository
) {
    fun createTodo(request: CreateTodoRequest) : TodoResponse {
        val modelToSave = request.toModel()
        val entityToSave = modelToSave.toEntity()
        val savedTodo = todoRepository.save(entityToSave)
        return savedTodo.toResponse()
    }
}
