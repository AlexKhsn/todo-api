package com.example.todoapi.service

import com.example.todoapi.dto.UpdateTodoRequest
import com.example.todoapi.entity.Priority
import com.example.todoapi.exception.CustomExceptions.TodoNotFoundException
import com.example.todoapi.models.TodoModel
import com.example.todoapi.models.toEntity
import com.example.todoapi.models.toModel
import com.example.todoapi.repository.TodoRepository
import com.example.todoapi.testUtil.TestDataBuilder
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDateTime
import java.util.Optional

class TodoServiceUT : FunSpec({
    val mockRepository = mockk<TodoRepository>()
    val service = TodoService(mockRepository)

    beforeEach {
        clearMocks(mockRepository)
    }

    test("Should create todo and return model with all exist fields") {
        //  ARRANGE
        val request = TestDataBuilder.requestFilled()
        val entity = TestDataBuilder.entityToSaveFilled()

        every { mockRepository.save(any()) } returns entity

        //  ACT
        val result = service.createTodo(request)

        //  ASSERT
        result.title shouldBe request.title
        result.description shouldBe request.description
        result.completed shouldBe request.completed
        result.priority shouldBe request.priority

        result.id shouldBe entity.id
        result.createdAt shouldBe entity.createdAt
        result.updatedAt shouldBe entity.updatedAt

        //  VERIFY
        verify(exactly = 1) { mockRepository.save(any()) }
    }

    test("Should create todo and return model with default values") {
        //  ARRANGE
        val request = TestDataBuilder.requestDefault()
        val entity = TestDataBuilder.entityToSaveDefault()

        every { mockRepository.save(any()) } returns entity

        //  ACT
        val result = service.createTodo(request)

        //  ASSERT
        result.title shouldBe request.title
        result.description shouldBe null
        result.completed shouldBe false
        result.priority shouldBe Priority.MEDIUM

        result.id shouldBe entity.id
        result.createdAt shouldBe entity.createdAt
        result.updatedAt shouldBe entity.updatedAt

        //  VERIFY
        verify(exactly = 1) { mockRepository.save(any()) }
    }

    test("Should return todo model when search by existing ID") {
        //  ARRANGE
        val existingId = 1L
        val foundEntity = TestDataBuilder.entitySavedFilled()

        every { mockRepository.findById(existingId) } returns Optional.of(foundEntity)

        //  ACT
        val result = service.getTodoById(existingId)

        //  ASSERT
        result.id shouldBe foundEntity.id
        result.title shouldBe foundEntity.title
        result.description shouldBe foundEntity.description
        result.completed shouldBe foundEntity.completed
        result.priority shouldBe foundEntity.priority
        result.createdAt shouldBe foundEntity.createdAt
        result.updatedAt shouldBe foundEntity.updatedAt

        //  VERIFY
        verify(exactly = 1) { mockRepository.findById(existingId) }
    }

    test("Should throw exception when search by non-existing ID") {
        //  ARRANGE
        val nonExistingId = 1L
        every { mockRepository.findById(nonExistingId) } returns Optional.empty()

        //  ACT
        val exception =
            shouldThrow<TodoNotFoundException> {
                service.getTodoById(nonExistingId)
            }

        //  ASSERT
        exception.message shouldBe "Todo with id: $nonExistingId not found"

        //  VERIFY
        verify(exactly = 1) { mockRepository.findById(nonExistingId) }
    }

    test("Should return a list of models when entities exist") {
        //  ARRANGE
        val entities = TestDataBuilder.listOfEntities()
        every { mockRepository.findAll() } returns entities

        //  ACT
        val result = service.getAllTodos()

        //  ASSERT
        result shouldNotBe emptyList<TodoModel>()
        result.size shouldBe entities.size
        result.forEachIndexed { index, model ->
            model.id shouldBe entities[index].id
            model.title shouldBe entities[index].title
            model.description shouldBe entities[index].description
            model.completed shouldBe entities[index].completed
            model.priority shouldBe entities[index].priority
            model.createdAt shouldBe entities[index].createdAt
            model.updatedAt shouldBe entities[index].updatedAt
        }

        //  VERIFY
        verify(exactly = 1) { mockRepository.findAll() }
    }

    test("Should return an empty list when entities absent") {
        //  ARRANGE
        every { mockRepository.findAll() } returns emptyList()

        //  ACT
        val result = service.getAllTodos()

        //  ASSERT
        result shouldBe emptyList<TodoModel>()
        result.size shouldBe 0

        //  VERIFY
        verify(exactly = 1) { mockRepository.findAll() }
    }

    test("Should return a list of completed models when such exist") {
        //  ARRANGE
        val entities =
            listOf(
                TestDataBuilder.entitySavedFilled(),
                TestDataBuilder.entitySavedFilled(id = 2L),
            )
        every { mockRepository.findByCompleted(true) } returns entities

        //  ACT
        val result = service.getAllTodosByCompleted(true)

        //  ASSERT
        result.size shouldBe entities.size
        result.onEach { model -> model.completed shouldBe true }

        //  VERIFY
        verify(exactly = 1) { mockRepository.findByCompleted(true) }
    }

    test("Should return an empty list when completed entities absent") {
        //  ARRANGE
        every { mockRepository.findByCompleted(true) } returns emptyList()

        //  ACT
        val result = service.getAllTodosByCompleted(true)

        //  ASSERT
        result shouldBe emptyList<TodoModel>()
        result.size shouldBe 0

        //  VERIFY
        verify(exactly = 1) { mockRepository.findByCompleted(true) }
    }

    test("Should update and return updated model when update by existing ID and filled request") {
        //  ARRANGE
        val existingId = 1L
        val foundEntity = TestDataBuilder.entitySavedDefault()
        val foundModel = foundEntity.toModel()
        val requestFilled =
            UpdateTodoRequest(
                title = "updated test title",
                description = "updated test description",
                completed = true,
                priority = Priority.LOW,
            )
        val updatedEntity =
            foundModel.copy(
                title = requestFilled.title!!,
                description = requestFilled.description,
                completed = requestFilled.completed!!,
                priority = requestFilled.priority!!,
                updatedAt = LocalDateTime.now(),
            ).toEntity()

        every { mockRepository.findById(existingId) } returns Optional.of(foundEntity)
        every { mockRepository.save(any()) } returns updatedEntity

        //  ACT
        val result = service.updateTodo(existingId, requestFilled)

        //  ASSERT
        result.title shouldBe requestFilled.title
        result.description shouldBe requestFilled.description
        result.completed shouldBe requestFilled.completed
        result.priority shouldBe requestFilled.priority

        result.id shouldBe foundEntity.id
        result.createdAt shouldBe foundEntity.createdAt
        result.updatedAt shouldBe updatedEntity.updatedAt

        //  VERIFY
        verify(exactly = 1) { mockRepository.findById(existingId) }
        verify(exactly = 1) { mockRepository.save(any()) }
    }
})
