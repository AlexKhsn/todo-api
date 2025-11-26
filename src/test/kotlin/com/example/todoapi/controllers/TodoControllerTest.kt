package com.example.todoapi.controllers

import com.example.todoapi.exception.CustomExceptions
import com.example.todoapi.models.TodoModel
import com.example.todoapi.models.toModel
import com.example.todoapi.service.TodoService
import com.example.todoapi.testUtil.TestDataBuilder
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
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
    }
}
