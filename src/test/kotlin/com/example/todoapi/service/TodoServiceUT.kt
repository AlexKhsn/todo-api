package com.example.todoapi.service

import com.example.todoapi.dto.UpdateTodoRequest
import com.example.todoapi.entity.Priority
import com.example.todoapi.entity.Todo
import com.example.todoapi.exception.CustomExceptions.TodoNotFoundException
import com.example.todoapi.models.TodoModel
import com.example.todoapi.models.toEntity
import com.example.todoapi.models.toModel
import com.example.todoapi.repository.TodoRepository
import com.example.todoapi.testUtil.TestDataBuilder
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.exactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDateTime
import java.util.Optional
import org.mockito.kotlin.any
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

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

    test("Should return a list of models when entities exist and no parameters passed") {
        //  ARRANGE
        val entities = TestDataBuilder.listOfEntities()
        val page = PageImpl(entities, Pageable.unpaged(), entities.size.toLong())
        every {
            mockRepository.findWithFilters(
                completed = null,
                subtitle = null,
                priority = null,
            )
        } returns page

        //  ACT
        val result =
            service.getTodos(
                completed = null,
                subtitle = null,
                priority = null,
            )

        //  ASSERT
        result.content shouldNotBe emptyList<TodoModel>()
        result.content.size shouldBe entities.size
        result.content.forEachIndexed { index, model ->
            model.id shouldBe entities[index].id
            model.title shouldBe entities[index].title
            model.description shouldBe entities[index].description
            model.completed shouldBe entities[index].completed
            model.priority shouldBe entities[index].priority
            model.createdAt shouldBe entities[index].createdAt
            model.updatedAt shouldBe entities[index].updatedAt
        }

        //  VERIFY
        verify(exactly = 1) { mockRepository.findWithFilters(any(), any(), any(), any()) }
    }

    test("Should return an empty list when entities absent and no parameters passed") {
        //  ARRANGE
        val page = PageImpl(emptyList<Todo>(), Pageable.unpaged(), 0)
        every {
            mockRepository.findWithFilters(
                completed = null,
                subtitle = null,
                priority = null,
            )
        } returns page

        //  ACT
        val result =
            service.getTodos(
                completed = null,
                subtitle = null,
                priority = null,
            )

        //  ASSERT
        result.content shouldBe emptyList<TodoModel>()
        result.content.size shouldBe 0

        //  VERIFY
        verify(exactly = 1) { mockRepository.findWithFilters(any(), any(), any()) }
    }

    test("Should return a list of completed models when such exist and completed parameter passed") {
        //  ARRANGE
        val entities =
            listOf(
                TestDataBuilder.entitySavedFilled(),
                TestDataBuilder.entitySavedFilled(id = 2L),
            )
        val page = PageImpl(entities, Pageable.unpaged(), entities.size.toLong())
        every {
            mockRepository.findWithFilters(
                completed = true,
                subtitle = null,
                priority = null,
            )
        } returns page

        //  ACT
        val result =
            service.getTodos(
                completed = true,
                subtitle = null,
                priority = null,
            )

        //  ASSERT
        result.content.size shouldBe entities.size
        result.content.onEach { model -> model.completed shouldBe true }

        //  VERIFY
        verify(exactly = 1) { mockRepository.findWithFilters(true, null, null) }
    }

    test("Should return an empty list when completed entities absent") {
        //  ARRANGE
        val page = PageImpl(emptyList<Todo>(), Pageable.unpaged(), 0)
        every {
            mockRepository.findWithFilters(
                completed = true,
                subtitle = null,
                priority = null,
            )
        } returns page

        //  ACT
        val result =
            service.getTodos(
                completed = true,
                subtitle = null,
                priority = null,
            )

        //  ASSERT
        result.content shouldBe emptyList<TodoModel>()
        result.content.size shouldBe 0

        //  VERIFY
        verify(exactly = 1) { mockRepository.findWithFilters(true, null, null) }
    }

    test("Should return a list of models which titles contain subtitle with 1 parameter passed") {
        //  ARRANGE
        val entities =
            listOf(
                TestDataBuilder.entitySavedFilled(),
                TestDataBuilder.entitySavedFilled(id = 2L),
            )
        val page = PageImpl(entities, Pageable.unpaged(), entities.size.toLong())
        every {
            mockRepository.findWithFilters(
                completed = null,
                subtitle = "tEsT",
                priority = null,
            )
        } returns page

        //  ACT
        val result =
            service.getTodos(
                completed = null,
                subtitle = "tEsT",
                priority = null,
            )

        //  ASSERT
        result.content.size shouldBe entities.size
        result.content.onEach { model -> model.title shouldContain "tEsT".lowercase() }

        //  VERIFY
        verify(exactly = 1) { mockRepository.findWithFilters(null, "tEsT", null) }
    }

    test("Should return an empty list when no entities contain subtitle in titles") {
        //  ARRANGE
        val page = PageImpl(emptyList<Todo>(), Pageable.unpaged(), 0)
        every {
            mockRepository.findWithFilters(
                completed = null,
                subtitle = "tEsT",
                priority = null,
            )
        } returns page

        //  ACT
        val result =
            service.getTodos(
                completed = null,
                subtitle = "tEsT",
                priority = null,
            )

        //  ASSERT
        result.content shouldBe emptyList<TodoModel>()
        result.content.size shouldBe 0

        //  VERIFY
        verify(exactly = 1) { mockRepository.findWithFilters(null, "tEsT", null) }
    }

    test("Should return a list of completed models containing subtitle when 2 parameters passed") {
        //  ARRANGE
        val entities =
            listOf(
                TestDataBuilder.entitySavedFilled(),
                TestDataBuilder.entitySavedFilled(id = 2L),
            )
        val page = PageImpl(entities, Pageable.unpaged(), entities.size.toLong())
        every {
            mockRepository.findWithFilters(
                completed = true,
                subtitle = "tESt",
                priority = null,
            )
        } returns page

        //  ACT
        val result =
            service.getTodos(
                completed = true,
                subtitle = "tESt",
                priority = null,
            )

        //  ASSERT
        result.content.size shouldBe entities.size
        result.content.onEach { model -> model.completed shouldBe true }
        result.content.onEach { model -> model.title shouldContain "tESt".lowercase() }

        //  VERIFY
        verify(exactly = 1) { mockRepository.findWithFilters(true, "tESt", null) }
    }

    test("Should return an empty list when no completed entities contain subtitle in titles with 2 parameters") {
        //  ARRANGE
        val page = PageImpl(emptyList<Todo>(), Pageable.unpaged(), 0)
        every {
            mockRepository.findWithFilters(
                completed = true,
                subtitle = "tEsT",
                priority = null,
            )
        } returns page

        //  ACT
        val result =
            service.getTodos(
                completed = true,
                subtitle = "tEsT",
                priority = null,
            )

        //  ASSERT
        result.content shouldBe emptyList<TodoModel>()
        result.content.size shouldBe 0

        //  VERIFY
        verify(exactly = 1) { mockRepository.findWithFilters(true, "tEsT", null) }
    }

    test("Should throw exception when update by existing ID and blank title in request") {
        // ARRANGE
        val existingId = 1L
        val updateRequest =
            UpdateTodoRequest(
                title = " ",
                description = null,
                completed = null,
                priority = null,
            )

        //  ACT
        val exception =
            shouldThrow<IllegalArgumentException> {
                service.updateTodo(existingId, updateRequest)
            }

        //  ASSERT
        exception.message shouldBe "Title must not be empty!"

        //  VERIFY
        verify(exactly = 0) { mockRepository.findById(existingId) }
        verify(exactly = 0) { mockRepository.save(any()) }
    }

    test("Should throw exception when update by non-existing ID") {
        //  ARRANGE
        val nonExistingId = 1L
        val updateFilledRequest =
            UpdateTodoRequest(
                title = "updated test title",
                description = "updated test description",
                completed = true,
                priority = Priority.LOW,
            )

        every { mockRepository.findById(nonExistingId) } returns Optional.empty()

        //  ACT
        val exception =
            shouldThrow<TodoNotFoundException> {
                service.updateTodo(nonExistingId, updateFilledRequest)
            }

        //  ASSERT
        exception.message shouldBe "Todo with id: $nonExistingId not found"

        //  VERIFY
        verify(exactly = 1) { mockRepository.findById(nonExistingId) }
        verify(exactly = 0) { mockRepository.save(any()) }
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

    test("Should update and return updated model when update by existing ID and half-filled request") {
        //  ARRANGE
        val existingId = 1L
        val foundEntity = TestDataBuilder.entitySavedDefault()
        val foundModel = foundEntity.toModel()
        val requestHalfFilled =
            UpdateTodoRequest(
                title = "updated test title",
                description = null,
                completed = null,
                priority = null,
            )
        val updatedEntity =
            foundModel.copy(
                title = requestHalfFilled.title!!,
                updatedAt = LocalDateTime.now(),
            ).toEntity()

        every { mockRepository.findById(existingId) } returns Optional.of(foundEntity)
        every { mockRepository.save(any()) } returns updatedEntity

        //  ACT
        val result = service.updateTodo(existingId, requestHalfFilled)

        //  ASSERT
        result.id shouldBe foundEntity.id
        result.description shouldBe foundEntity.description
        result.completed shouldBe foundEntity.completed
        result.priority shouldBe foundEntity.priority
        result.createdAt shouldBe foundEntity.createdAt

        result.title shouldBe requestHalfFilled.title
        result.updatedAt shouldBe updatedEntity.updatedAt

        //  VERIFY
        verify(exactly = 1) { mockRepository.findById(existingId) }
        verify(exactly = 1) { mockRepository.save(any()) }
    }

    test("Should not update and return founded model when update by existing ID and non-filled request") {
        //  ARRANGE
        val existingId = 1L
        val foundedEntity = TestDataBuilder.entitySavedDefault()
        val updateEmptyRequest =
            UpdateTodoRequest(
                title = null,
                description = null,
                completed = null,
                priority = null,
            )

        every { mockRepository.findById(existingId) } returns Optional.of(foundedEntity)

        //  ACT
        val result = service.updateTodo(existingId, updateEmptyRequest)

        //  ASSERT
        result.id shouldBe foundedEntity.id
        result.title shouldBe foundedEntity.title
        result.description shouldBe foundedEntity.description
        result.completed shouldBe foundedEntity.completed
        result.priority shouldBe foundedEntity.priority
        result.createdAt shouldBe foundedEntity.createdAt
        result.updatedAt shouldBe foundedEntity.updatedAt

        //  VERIFY
        verify(exactly = 1) { mockRepository.findById(existingId) }
        verify(exactly = 0) { mockRepository.save(any()) }
    }

    test("Should toggle todo complete to true when it's false and return updated model") {
        //  ARRANGE
        val existingId = 1L
        val foundedEntity = TestDataBuilder.entitySavedDefault()
        val model = foundedEntity.toModel()
        val updatedEntity =
            model.copy(
                completed = true,
                updatedAt = LocalDateTime.now(),
            ).toEntity()

        every { mockRepository.findById(existingId) } returns Optional.of(foundedEntity)
        every { mockRepository.save(any()) } returns updatedEntity

        //  ACT
        val result = service.toggleComplete(existingId)

        //  ASSERT
        result.completed shouldBe true
        result.updatedAt shouldBe updatedEntity.updatedAt

        //  VERIFY
        verify(exactly = 1) { mockRepository.findById(existingId) }
        verify(exactly = 1) { mockRepository.save(any()) }
    }

    test("Should toggle todo complete to false when it's true and return updated model") {
        //  ARRANGE
        val existingId = 1L
        val foundedEntity = TestDataBuilder.entitySavedFilled()
        val model = foundedEntity.toModel()
        val updatedEntity =
            model.copy(
                completed = false,
                updatedAt = LocalDateTime.now(),
            ).toEntity()

        every { mockRepository.findById(existingId) } returns Optional.of(foundedEntity)
        every { mockRepository.save(any()) } returns updatedEntity

        //  ACT
        val result = service.toggleComplete(existingId)

        //  ASSERT
        result.completed shouldBe false
        result.updatedAt shouldBe updatedEntity.updatedAt

        //  VERIFY
        verify(exactly = 1) { mockRepository.findById(existingId) }
        verify(exactly = 1) { mockRepository.save(any()) }
    }

    test("Should throw exception when toggle todo completed by non-existing ID") {
        //  ARRANGE
        val nonExistingId = 1L

        every { mockRepository.findById(nonExistingId) } returns Optional.empty()

        //  ACT
        val exception =
            shouldThrow<TodoNotFoundException> {
                service.toggleComplete(nonExistingId)
            }

        //  ASSERT
        exception.message shouldBe "Todo with id: $nonExistingId not found"

        //  VERIFY
        verify(exactly = 1) { mockRepository.findById(nonExistingId) }
        verify(exactly = 0) { mockRepository.save(any()) }
    }

    test("Should delete todo and return it by existing ID") {
        //  ARRANGE
        val existingId = 1L
        val foundedEntity = TestDataBuilder.entitySavedDefault()

        every { mockRepository.findById(existingId) } returns Optional.of(foundedEntity)
        every { mockRepository.deleteById(existingId) } returns Unit

        //  ACT
        val result = service.deleteTodo(existingId)

        //  ASSERT
        result.id shouldBe foundedEntity.id
        result.title shouldBe foundedEntity.title
        result.description shouldBe foundedEntity.description
        result.completed shouldBe foundedEntity.completed
        result.priority shouldBe foundedEntity.priority
        result.createdAt shouldBe foundedEntity.createdAt
        result.updatedAt shouldBe foundedEntity.updatedAt

        //  VERIFY
        verify(exactly = 1) { mockRepository.findById(existingId) }
        verify(exactly = 1) { mockRepository.deleteById(existingId) }
    }

    test("Should throw exception when delete todo by non-existing ID") {
        //  ARRANGE
        val nonExistingId = 1L

        every { mockRepository.findById(nonExistingId) } returns Optional.empty()

        //  ACT
        val exception =
            shouldThrow<TodoNotFoundException> {
                service.deleteTodo(nonExistingId)
            }

        //  ASSERT
        exception.message shouldBe "Todo with id: $nonExistingId not found"

        //  VERIFY
        verify(exactly = 1) { mockRepository.findById(nonExistingId) }
        verify(exactly = 0) { mockRepository.deleteById(nonExistingId) }
    }

    test("Should return requested page with correct size and metadata") {
        //  ARRANGE
        val entities = TestDataBuilder.listOfEntities()
        val pageable = PageRequest.of(0, 2)
        val page = PageImpl(entities.take(2), pageable, 4)

        every { mockRepository.findWithFilters(null, null, null, pageable) } returns page

        //  ACT
        val result = service.getTodos(null, null, null, pageable)

        //  ASSERT
        result.content.size shouldBe 2
        result.totalElements shouldBe entities.size
        result.totalPages shouldBe 2
        result.number shouldBe 0
        result.isFirst shouldBe true
        result.isLast shouldBe false

        //  VERIFY
        verify(exactly = 1) { mockRepository.findWithFilters(null, null, null, pageable) }
    }

    test("Should pass sorting parameters to repository") {
        //  ARRANGE
        val pageable = PageRequest.of(0, 10, Sort.by("title").ascending())
        val page = PageImpl(emptyList<Todo>(), pageable, 0)

        every { mockRepository.findWithFilters(null, null, null, pageable) } returns page

        //  ACT
        service.getTodos(null, null, null, pageable)

        //  ASSERT & VERIFY
        verify(exactly = 1) { mockRepository.findWithFilters(null, null, null, pageable) }
    }

    test("Should handle multiple sort criteria") {
        //  ARRANGE
        val sort = Sort.by(Sort.Order.desc("priority"), Sort.Order.asc("createdAt"))
        val pageable = PageRequest.of(0, 10, sort)
        val page = PageImpl(emptyList<Todo>(), pageable, 0)

        every { mockRepository.findWithFilters(null, null, null, pageable) } returns page

        //  ACT
        service.getTodos(null, null, null, pageable)

        //  ASSERT & VERIFY
        verify(exactly = 1) { mockRepository.findWithFilters(null, null, null, pageable) }
    }

    test("Should apply sorting together with filters") {
        //  ARRANGE
        val sort = Sort.by(Sort.Order.asc("createdAt"), Sort.Order.desc("priority"))
        val pageable = PageRequest.of(0, 10, sort)
        val page = PageImpl(emptyList<Todo>(), pageable, 0)

        every { mockRepository.findWithFilters(true, "test", null, pageable) } returns page

        //  ACT
        service.getTodos(true, "test", null, pageable)

        //  ASSERT & VERIFY
        verify(exactly = 1) { mockRepository.findWithFilters(true, "test", null, pageable) }
    }

    test("Should handle unsorted pageable") {
        //  ARRANGE
        val pageable = PageRequest.of(0, 10)
        val page = PageImpl(emptyList<Todo>(), pageable, 0)

        every { mockRepository.findWithFilters(null, null, null, pageable) } returns page

        //  ACT
        service.getTodos(null, null, null, pageable)

        //  ASSERT & VERIFY
        verify(exactly = 1) { mockRepository.findWithFilters(null, null, null, pageable) }
    }

    test("Should pass priority parameter to repository without modification") {
        //  ARRANGE
        val pageable = PageRequest.of(0, 10)
        val page = PageImpl(emptyList<Todo>(), pageable, 0)

        every { mockRepository.findWithFilters(null, null, Priority.HIGH, pageable) } returns page

        //  ACT
        service.getTodos(null, null, Priority.HIGH, pageable)

        //  ASSERT & VERIFY
        verify(exactly = 1) { mockRepository.findWithFilters(null, null, Priority.HIGH, pageable) }
    }

    test("Should handle null priority parameter") {
        //  ARRANGE
        val pageable = PageRequest.of(0, 10)
        val page = PageImpl(emptyList<Todo>(), pageable, 0)

        every { mockRepository.findWithFilters(null, null, null, pageable) } returns page

        //  ACT
        service.getTodos(null, null, null, pageable)

        //  ASSERT & VERIFY
        verify(exactly = 1) { mockRepository.findWithFilters(null, null, null, pageable) }
    }

    test("Should pass priority with other filters") {
        //  ARRANGE
        val pageable = PageRequest.of(0, 10)
        val page = PageImpl(emptyList<Todo>(), pageable, 0)

        every { mockRepository.findWithFilters(true, "test", Priority.HIGH, pageable) } returns page

        //  ACT
        service.getTodos(true, "test", Priority.HIGH, pageable)

        //  ASSERT & VERIFY
        verify(exactly = 1) { mockRepository.findWithFilters(true, "test", Priority.HIGH, pageable) }
    }

    test("Should throw exception when ids list is empty") {
        //  ARRANGE
        val emptyIds = emptyList<Long>()

        //  ACT
        val exception =
            shouldThrow<IllegalArgumentException> {
                service.bulkDeleteTodos(emptyIds)
            }

        //  ASSERT
        exception.message shouldBe "Ids must not be empty!"

        //  VERIFY
        verify(exactly = 0) { mockRepository.deleteByIdIn(emptyIds) }
    }

    test("Should throw exception when one id not found") {
        //  ARRANGE
        val ids = listOf(1L, 2L)
        val entity1 = TestDataBuilder.entitySavedDefault()

        every { mockRepository.findById(1L) } returns Optional.of(entity1)
        every { mockRepository.findById(2L) } returns Optional.empty()

        //  ACT
        val exception =
            shouldThrow<TodoNotFoundException> {
                service.bulkDeleteTodos(ids)
            }

        //  ASSERT
        exception.message shouldBe "Todo with id: 2 not found"

        //  VERIFY
        verify(exactly = 2) { mockRepository.findById(any()) }
        verify(exactly = 0) { mockRepository.deleteByIdIn(ids) }
    }

    test("Should call deleteByIdIn when all ids exist") {
        //  ARRANGE
        val ids = listOf(1L, 2L, 3L)
        val entity1 = TestDataBuilder.entitySavedDefault()
        val entity2 = TestDataBuilder.entitySavedDefault(id = 2L)
        val entity3 = TestDataBuilder.entitySavedDefault(id = 3L)

        every { mockRepository.findById(1L) } returns Optional.of(entity1)
        every { mockRepository.findById(2L) } returns Optional.of(entity2)
        every { mockRepository.findById(3L) } returns Optional.of(entity3)
        every { mockRepository.deleteByIdIn(any()) } returns 3

        //  ACT
        val result = service.bulkDeleteTodos(ids)

        //  ASSERT
        result shouldBe 3

        //  VERIFY
        verify(exactly = 3) { mockRepository.findById(any()) }
        verify(exactly = 1) { mockRepository.deleteByIdIn(ids) }
    }

    test("Should remove duplicates before deleting") {
        //  ARRANGE
        val duplicatedIds = listOf(1L, 2L, 1L)
        val entity1 = TestDataBuilder.entitySavedDefault()
        val entity2 = TestDataBuilder.entitySavedDefault(id = 2L)

        every { mockRepository.findById(1L) } returns Optional.of(entity1)
        every { mockRepository.findById(2L) } returns Optional.of(entity2)
        every { mockRepository.deleteByIdIn(any()) } returns 2

        //  ACT
        val result = service.bulkDeleteTodos(duplicatedIds)

        //  ASSERT
        result shouldBe 2

        //  VERIFY
        verify(exactly = 2) { mockRepository.findById(any()) }
        verify(exactly = 1) { mockRepository.deleteByIdIn(listOf(1L, 2L)) }
    }

    test("Should throw exception when ids list is empty when bulk updating") {
        //  ARRANGE
        val ids = emptyList<Long>()

        //  ACT
        val exception =
            shouldThrow<IllegalArgumentException> {
                service.bulkUpdateTodos(ids, any(), any())
            }

        //  ASSERT
        exception.message shouldBe "Ids cannot be empty!"

        //  VERIFY
        verify(exactly = 0) { mockRepository.updateByIdIn(ids, any(), any()) }
    }

    test("Should throw exception when todo not found") {
        //  ARRANGE
        val ids = listOf(1L)

        every { mockRepository.findById(1L) } returns Optional.empty()

        //  ACT
        val exception =
            shouldThrow<TodoNotFoundException> {
                service.bulkUpdateTodos(ids, any(), any())
            }

        //  ASSERT
        exception.message shouldBe "Todo with id: 1 not found"

        //  VERIFY
        verify(exactly = 1) { mockRepository.findById(1L) }
        verify(exactly = 0) { mockRepository.updateByIdIn(ids, any(), any()) }
    }

    test("Should return unchanged models when both params are null") {
        //  ARRANGE
        val ids = listOf(1L, 2L)
        val entity1 = TestDataBuilder.entitySavedDefault()
        val entity2 = TestDataBuilder.entitySavedDefault(id = 2L)

        every { mockRepository.findById(1L) } returns Optional.of(entity1)
        every { mockRepository.findById(2L) } returns Optional.of(entity2)

        //  ACT
        val result = service.bulkUpdateTodos(ids, null, null)

        //  ASSERT
        result[0].updatedAt.toString() shouldBe entity1.updatedAt.toString()
        result[1].updatedAt.toString() shouldBe entity2.updatedAt.toString()

        //  VERIFY
        verify(exactly = 0) { mockRepository.updateByIdIn(ids, null, null) }
        verify(exactly = 0) { mockRepository.findAllById(ids) }
    }

    test("Should call updateByIdIn when completed is provided") {
        //  ARRANGE
        val ids = listOf(1L, 2L)
        val entity1 = TestDataBuilder.entitySavedDefault()
        val entity2 = TestDataBuilder.entitySavedDefault(id = 2L)
        val updatedAt = LocalDateTime.now()
        val updatedEntity1 = TestDataBuilder.entitySavedDefault(completed = true, updatedAt = updatedAt)
        val updatedEntity2 = TestDataBuilder.entitySavedDefault(id = 2L, completed = true, updatedAt = updatedAt)

        every { mockRepository.findById(1L) } returns Optional.of(entity1)
        every { mockRepository.findById(2L) } returns Optional.of(entity2)
        every { mockRepository.updateByIdIn(any(), any(), any()) } returns 2
        every { mockRepository.findAllById(ids) } returns listOf(updatedEntity1, updatedEntity2)

        //  ACT
        val result = service.bulkUpdateTodos(ids, true, null)

        //  ASSERT
        result.all { it.completed } shouldBe true

        //  VERIFY
        verify(exactly = 2) { mockRepository.findById(any()) }
        verify(exactly = 1) { mockRepository.updateByIdIn(ids, true, null) }
        verify(exactly = 1) { mockRepository.findAllById(ids) }
    }

    test("Should call updateByIdIn when both params provided") {
        //  ARRANGE
        val ids = listOf(1L, 2L)
        val entity1 = TestDataBuilder.entitySavedDefault()
        val entity2 = TestDataBuilder.entitySavedDefault(id = 2L)
        val updatedAt = LocalDateTime.now()
        val updatedEntity1 =
            TestDataBuilder.entitySavedDefault(
                completed = true,
                priority = Priority.HIGH,
                updatedAt = updatedAt,
            )
        val updatedEntity2 =
            TestDataBuilder.entitySavedDefault(
                id = 2L,
                completed = true,
                priority = Priority.HIGH,
                updatedAt = updatedAt,
            )

        every { mockRepository.findById(1L) } returns Optional.of(entity1)
        every { mockRepository.findById(2L) } returns Optional.of(entity2)
        every { mockRepository.updateByIdIn(any(), any(), any()) } returns 2
        every { mockRepository.findAllById(ids) } returns listOf(updatedEntity1, updatedEntity2)

        //  ACT
        val result = service.bulkUpdateTodos(ids, true, Priority.HIGH)

        //  ASSERT
        result.all { it.completed } shouldBe true
        result.all { it.priority == Priority.HIGH } shouldBe true

        //  VERIFY
        verify(exactly = 2) { mockRepository.findById(any()) }
        verify(exactly = 1) { mockRepository.updateByIdIn(ids, true, Priority.HIGH) }
        verify(exactly = 1) { mockRepository.findAllById(ids) }
    }

    test("Should remove duplicates from ids list") {
        //  ARRANGE
        val ids = listOf(1L, 2L, 1L)
        val entity1 = TestDataBuilder.entitySavedDefault()
        val entity2 = TestDataBuilder.entitySavedDefault(id = 2L)
        val updatedAt = LocalDateTime.now()
        val updatedEntity1 = TestDataBuilder.entitySavedDefault(completed = true, updatedAt = updatedAt)
        val updatedEntity2 = TestDataBuilder.entitySavedDefault(id = 2L, completed = true, updatedAt = updatedAt)

        every { mockRepository.findById(1L) } returns Optional.of(entity1)
        every { mockRepository.findById(2L) } returns Optional.of(entity2)
        every { mockRepository.updateByIdIn(any(), any(), any()) } returns 2
        every { mockRepository.findAllById(any()) } returns listOf(updatedEntity1, updatedEntity2)

        //  ACT
        val result = service.bulkUpdateTodos(ids, true, null)

        //  ASSERT
        result.size shouldBe 2
        result.all { it.completed } shouldBe true

        //  VERIFY
        verify { mockRepository.updateByIdIn(listOf(1L, 2L), any(), any()) }
    }
})
