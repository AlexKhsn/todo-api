package com.example.todoapi.service

import com.example.todoapi.dto.CreateTodoRequest
import com.example.todoapi.entity.Priority
import com.example.todoapi.entity.Todo
import com.example.todoapi.repository.TodoRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDateTime

class TodoServiceUT : FunSpec({
    val mockRepository = mockk<TodoRepository>()
    val mockTodoService = TodoService(mockRepository)

    test("Should create todo and return model when all fields exist") {
        //  ARRANGE
        val request =
            CreateTodoRequest(
                title = "test",
                description = "test",
                completed = true,
                priority = Priority.HIGH,
            )
        val entity =
            Todo(
                id = 1L,
                title = request.title,
                description = request.description,
                completed = request.completed!!,
                priority = request.priority!!,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
            )

        every { mockRepository.save(any()) } returns entity

        //  ACT
        val result = mockTodoService.createTodo(request)

        //  ASSERT
        result.id shouldNotBe null
        result.id shouldBe entity.id
        result.title shouldBe request.title
        result.description shouldBe request.description
        result.priority shouldBe request.priority
        result.completed shouldBe request.completed
        result.createdAt shouldBe entity.createdAt
        result.updatedAt shouldBe entity.updatedAt

        //  VERIFY
        verify { mockRepository.save(any()) }
    }
})
