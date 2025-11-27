package com.example.todoapi.controllers

import com.example.todoapi.entity.Priority
import com.example.todoapi.exception.CustomExceptions
import com.example.todoapi.models.TodoModel
import com.example.todoapi.models.toModel
import com.example.todoapi.service.TodoService
import com.example.todoapi.testUtil.TestDataBuilder
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.hibernate.query.Page.page
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(TodoController::class)
class TodoControllerTest : FunSpec() {
    @Autowired
    lateinit var mvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var todoService: TodoService

    init {
        test("GET /api/todos should route to getTodos method") {
            //  ARRANGE
            val page = PageImpl(emptyList<TodoModel>(), Pageable.unpaged(), 0)
            whenever(todoService.getTodos(eq(null), eq(null), eq(null), any())).thenReturn(page)

            //  ACT & ASSERT
            mvc.perform(get("/api/todos"))
                .andExpect(status().isOk)
        }

        test("GET /api/todos/999 should return 404 when todo doesn't exist") {
            //  ARRANGE
            whenever(todoService.getTodoById(999))
                .thenThrow(CustomExceptions.TodoNotFoundException(999))

            //  ACT & ASSERT
            mvc.perform(get("/api/todos/999"))
                .andExpect(status().isNotFound)
        }

        test("POST /api/todos should return 400 when title is empty") {
            //  ARRANGE
            val invalidJson = """{"title": "", "description": "test"}"""

            //  ACT & ASSERT
            mvc.perform(
                post("/api/todos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidJson),
            ).andExpect(status().isBadRequest)
        }

        test("POST /api/todos should return 201 CREATED") {
            //  ARRANGE
            val validJson = """{"title": "test", "description": "test"}"""
            val model = TestDataBuilder.entitySavedFilled().toModel()

            whenever(todoService.createTodo(any())).thenReturn(model)

            //  ACT & ASSERT
            mvc.perform(
                post("/api/todos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(validJson),
            )
                .andExpect(status().isCreated)
        }

        test("GET /api/todos?completed=true should call correct service method") {
            //  ARRANGE
            val page = PageImpl(emptyList<TodoModel>(), Pageable.unpaged(), 0)
            whenever(todoService.getTodos(eq(true), eq(null), eq(null), any())).thenReturn(page)

            //  ACT & ASSERT
            mvc.perform(get("/api/todos?completed=true"))
                .andExpect(status().isOk)

            //  VERIFY
            verify(todoService).getTodos(eq(true), eq(null), eq(null), any())
        }

        test("GET /api/todos?page=1&size=5 should pass pagination parameters to service") {
            //  ARRANGE
            val page = PageImpl(emptyList<TodoModel>(), PageRequest.of(1, 5), 0)
            whenever(todoService.getTodos(eq(null), eq(null), eq(null), any())).thenReturn(page)

            //  ACT & ASSERT
            mvc.perform(get("/api/todos?page=1&size=5"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.size").value(5))

            //  VERIFY
            val captor = argumentCaptor<Pageable>()
            verify(todoService).getTodos(eq(null), eq(null), eq(null), captor.capture())

            val capturedPageable = captor.firstValue
            capturedPageable.pageNumber shouldBe 1
            capturedPageable.pageSize shouldBe 5
        }

        test("GET /api/todos without pagination params should use defaults") {
            // ARRANGE
            val page = PageImpl(emptyList<TodoModel>(), PageRequest.of(0, 20), 0)
            whenever(todoService.getTodos(eq(null), eq(null), eq(null), any())).thenReturn(page)

            // ACT & ASSERT
            mvc.perform(get("/api/todos"))
                .andExpect(status().isOk)

            // VERIFY - проверяем дефолтные значения Spring
            val captor = argumentCaptor<Pageable>()
            verify(todoService).getTodos(eq(null), eq(null), eq(null), captor.capture())

            val capturedPageable = captor.firstValue
            capturedPageable.pageNumber shouldBe 0 // Дефолтная страница 0
            capturedPageable.pageSize shouldBe 20 // Дефолтный размер 20
        }

        test("GET /api/todos?sort=title,asc should pass sorting to service") {
            //  ARRANGE
            val page = PageImpl(emptyList<TodoModel>(), PageRequest.of(0, 20), 0)
            val captor = argumentCaptor<Pageable>()

            whenever(todoService.getTodos(eq(null), eq(null), eq(null), any())).thenReturn(page)

            //  ACT & ASSERT
            mvc.perform(get("/api/todos?sort=title,asc"))
                .andExpect(status().isOk)

            //  VERIFY
            verify(todoService).getTodos(eq(null), eq(null), eq(null), captor.capture())
            val capturedPageable = captor.firstValue
            val sort = capturedPageable.sort
            sort.isSorted shouldBe true
            sort.getOrderFor("title")?.direction shouldBe Sort.Direction.ASC
        }

        test("GET /api/todos with multiple sort parameters") {
            //  ARRANGE
            val page = PageImpl(emptyList<TodoModel>(), PageRequest.of(0, 20), 0)
            val captor = argumentCaptor<Pageable>()

            whenever(todoService.getTodos(eq(null), eq(null), eq(null), any())).thenReturn(page)

            //  ACT & ASSERT
            mvc.perform(get("/api/todos?sort=priority,desc&sort=title,asc"))
                .andExpect(status().isOk)

            //  VERIFY
            verify(todoService).getTodos(eq(null), eq(null), eq(null), captor.capture())
            val capturedPageable = captor.firstValue
            val sort = capturedPageable.sort
            sort.getOrderFor("priority")?.direction shouldBe Sort.Direction.DESC
            sort.getOrderFor("title")?.direction shouldBe Sort.Direction.ASC
        }

        test("GET /api/todos with pagination and sorting combined") {
            //  ARRANGE
            val page = PageImpl(emptyList<TodoModel>(), PageRequest.of(0, 20), 0)
            val captor = argumentCaptor<Pageable>()

            whenever(todoService.getTodos(eq(null), eq(null), eq(null), any())).thenReturn(page)

            //  ACT & ASSERT
            mvc.perform(get("/api/todos?page=2&size=5&sort=createdAt,desc"))
                .andExpect(status().isOk)

            //  VERIFY
            verify(todoService).getTodos(eq(null), eq(null), eq(null), captor.capture())
            val capturedPageable = captor.firstValue
            capturedPageable.pageNumber shouldBe 2
            capturedPageable.pageSize shouldBe 5
            capturedPageable.sort.getOrderFor("createdAt")?.direction shouldBe Sort.Direction.DESC
        }

        test("GET /api/todos?priority=HIGH should call service with HIGH priority") {
            //  ARRANGE
            val page = PageImpl(emptyList<TodoModel>(), PageRequest.of(0, 20), 0)
            val captor = argumentCaptor<Pageable>()

            whenever(todoService.getTodos(eq(null), eq(null), eq(Priority.HIGH), any())).thenReturn(page)

            //  ACT & ASSERT
            mvc.perform(get("/api/todos?page=0&size=20&priority=HIGH"))
                .andExpect(status().isOk)

            //  VERIFY
            verify(todoService).getTodos(eq(null), eq(null), eq(Priority.HIGH), captor.capture())
        }

        test("GET /api/todos?priority=INVALID should return 400 Bad Request") {
            //  ACT & ASSERT
            mvc.perform(get("/api/todos?page=1&size=20&priority=INVALID"))
                .andExpect(status().isBadRequest)
        }

        test("GET /api/todos?priority=MEDIUM&completed=false&subtitle=work should pass all three filters") {
            //  ARRANGE
            val page = PageImpl(emptyList<TodoModel>(), PageRequest.of(0, 20), 0)

            whenever(todoService.getTodos(eq(false), eq("work"), eq(Priority.MEDIUM), any())).thenReturn(page)

            //  ACT & ASSERT
            mvc.perform(get("/api/todos?page=0&size=20&priority=MEDIUM&completed=false&subtitle=work"))
                .andExpect(status().isOk)

            //  VERIFY
            verify(todoService).getTodos(eq(false), eq("work"), eq(Priority.MEDIUM), any())
        }

        test("DELETE /api/todos/bulk?ids=1,2,3 should delete multiple todos and return count") {
            //  ARRANGE
            val ids = listOf(1L, 2L, 3L)

            whenever(todoService.bulkDeleteTodos(ids)).thenReturn(3)

            //  ACT & ASSERT
            mvc.perform(delete("/api/todos/bulk?ids=1,2,3"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.deleted").value(3))
        }

        test("DELETE /api/todos/bulk with empty ids should return 400") {
            //  ARRANGE
            val ids = emptyList<Long>()

            whenever(todoService.bulkDeleteTodos(ids)).thenThrow(IllegalArgumentException::class.java)

            //  ACT & ASSERT
            mvc.perform(delete("/api/todos/bulk?ids="))
                .andExpect(status().isBadRequest)
        }

        test("DELETE /api/todos/bulk?ids=1,999 when one id not found should return 404") {
            //  ARRANGE
            val ids = listOf(1L, 999L)
            whenever(todoService.bulkDeleteTodos(ids)).thenThrow(CustomExceptions.TodoNotFoundException(999))

            //  ACT & ASSERT
            mvc.perform(delete("/api/todos/bulk?ids=1,999"))
                .andExpect(status().isNotFound)
        }

        test("DELETE /api/todos/bulk without ids parameter should return 400") {
            //  ARRANGE
            whenever(todoService.bulkDeleteTodos(any())).thenThrow(IllegalArgumentException::class.java)

            //  ACT & ASSERT
            mvc.perform(delete("/api/todos/bulk"))
                .andExpect(status().isBadRequest)
        }

        test("DELETE /api/todos/bulk should pass ids as-is to service including duplicates") {
            //  ARRANGE
            val ids = listOf(1L, 2L, 1L)
            whenever(todoService.bulkDeleteTodos(ids)).thenReturn(2)

            //  ACT & ASSERT
            mvc.perform(delete("/api/todos/bulk?ids=1,2,1"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.deleted").value(2))

            //  VERIFY
            verify(todoService).bulkDeleteTodos(listOf(1, 2, 1))
        }

        test("DELETE /api/todos/bulk?ids=1&ids=2&ids=3 should parse multiple params") {
            //  ARRANGE
            val ids = listOf(1L, 2L, 3L)
            whenever(todoService.bulkDeleteTodos(ids)).thenReturn(3)

            //  ACT & ASSERT
            mvc.perform(delete("/api/todos/bulk?ids=1&ids=2&ids=3"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.deleted").value(3))
        }

        test("DELETE /api/todos/bulk should verify correct service method call") {
            //  ARRANGE
            val captor = argumentCaptor<List<Long>>()

            whenever(todoService.bulkDeleteTodos(any())).thenReturn(3)

            //  ACT
            mvc.perform(delete("/api/todos/bulk?ids=1,2,3"))
                .andExpect(status().isOk)

            //  VERIFY
            verify(todoService).bulkDeleteTodos(captor.capture())
            val capturedIds = captor.firstValue
            capturedIds shouldBe listOf(1L, 2L, 3L)
            capturedIds::class.java.simpleName shouldBe "ArrayList"
        }
    }
}
