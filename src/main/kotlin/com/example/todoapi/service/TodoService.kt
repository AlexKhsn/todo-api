package com.example.todoapi.service

import com.example.todoapi.dto.CreateTodoRequest
import com.example.todoapi.dto.UpdateTodoRequest
import com.example.todoapi.dto.toModel
import com.example.todoapi.entity.Priority
import com.example.todoapi.exception.CustomExceptions.TodoNotFoundException
import com.example.todoapi.models.TodoModel
import com.example.todoapi.models.toEntity
import com.example.todoapi.models.toModel
import com.example.todoapi.repository.TodoRepository
import java.time.LocalDateTime
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

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

    fun getTodos(
        completed: Boolean?,
        subtitle: String?,
        priority: Priority?,
        pageable: Pageable = Pageable.unpaged(),
    ): Page<TodoModel> {
        val foundEntities = todoRepository.findWithFilters(completed, subtitle, priority, pageable)
        return foundEntities.map { it.toModel() }
    }

    fun updateTodo(
        id: Long,
        request: UpdateTodoRequest,
    ): TodoModel {
        if (request.title != null && request.title.isBlank()) throw IllegalArgumentException("Title must not be empty!")

        val foundModel = getTodoById(id)

        if (
            request.title == null &&
            request.description == null &&
            request.completed == null &&
            request.priority == null
        ) {
            return foundModel
        }

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

    @Transactional
    fun bulkUpdateTodos(
        ids: List<Long>,
        completed: Boolean?,
        priority: Priority?,
    ): List<TodoModel> {
        if (ids.isEmpty()) throw IllegalArgumentException("Ids cannot be empty!")
        val uniqueIds = ids.distinct()
        val foundModels = uniqueIds.map { getTodoById(it) }
        if (completed == null && priority == null) return foundModels
        todoRepository.updateByIdIn(uniqueIds, completed, priority)
        val updatedModels = todoRepository.findAllById(uniqueIds).map { it.toModel() }
        return updatedModels
    }

    fun deleteTodo(id: Long): TodoModel {
        val foundModel = getTodoById(id)
        todoRepository.deleteById(id)
        return foundModel
    }

    @Transactional
    fun bulkDeleteTodos(ids: List<Long>): Int {
        if (ids.isEmpty()) throw IllegalArgumentException("Ids must not be empty!")
        val uniqueIds = ids.distinct()
        uniqueIds.forEach { getTodoById(it) }
        return todoRepository.deleteByIdIn(uniqueIds)
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
