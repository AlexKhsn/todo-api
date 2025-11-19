package com.example.todoapi.service

import com.example.todoapi.dto.CreateTodoRequest
import com.example.todoapi.dto.UpdateTodoRequest
import com.example.todoapi.dto.toModel
import com.example.todoapi.exception.CustomExceptions.TodoNotFoundException
import com.example.todoapi.models.TodoModel
import com.example.todoapi.models.toEntity
import com.example.todoapi.models.toModel
import com.example.todoapi.repository.TodoRepository
import java.time.LocalDateTime
import org.springframework.stereotype.Service

@Service
class TodoService(
    private val todoRepository: TodoRepository,
) {
    fun createTodo(request: CreateTodoRequest): TodoModel {
        val modelToSave = request.toModel()
        val entityToSave = modelToSave.toEntity()
        val savedEntity = todoRepository.save(entityToSave)
        return savedEntity.toModel()
    }

    fun getTodoById(id: Long): TodoModel {
        val foundEntity =
            todoRepository.findById(id).orElseThrow {
                TodoNotFoundException(id)
            }

        return foundEntity.toModel()
    }

    fun getAllTodos(): List<TodoModel> {
        val foundEntities = todoRepository.findAll()
        return foundEntities.map { it.toModel() }
    }

    fun getAllTodosByCompleted(completed: Boolean): List<TodoModel> {
        val foundEntities = todoRepository.findByCompleted(completed)
        return foundEntities.map { it.toModel() }
    }

    fun updateTodo(
        id: Long,
        request: UpdateTodoRequest,
    ): TodoModel {
        val foundModel = getTodoById(id)

        if (request.title != null && request.title.isBlank()) throw IllegalArgumentException("Title must not be empty!")

        val updatedModel =
            foundModel.copy(
                title = request.title ?: foundModel.title,
                description = request.description ?: foundModel.description,
                completed = request.completed ?: foundModel.completed,
                priority = request.priority ?: foundModel.priority,
                updatedAt = LocalDateTime.now(),
            )
        val updatedEntityToSave = updatedModel.toEntity()
        val updatedSavedEntity = todoRepository.save(updatedEntityToSave)
        return updatedSavedEntity.toModel()
    }

    fun deleteTodo(id: Long): TodoModel {
        val foundModel = getTodoById(id)
        todoRepository.deleteById(id)
        return foundModel
    }

    fun toggleComplete(id: Long): TodoModel {
        val foundModel = getTodoById(id)
        val updatedModel =
            foundModel.copy(
                completed = !foundModel.completed,
                updatedAt = LocalDateTime.now(),
            )
        val updatedEntityToSave = updatedModel.toEntity()
        val updatedSavedEntity = todoRepository.save(updatedEntityToSave)
        return updatedSavedEntity.toModel()
    }
}
