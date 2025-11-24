package com.example.todoapi.controllers

import com.example.todoapi.exception.CustomExceptions
import com.example.todoapi.models.toModel
import com.example.todoapi.service.TodoService
import com.example.todoapi.testUtil.TestDataBuilder
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.FunSpec
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.model
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
        test("GET /api/todos should route to getAllTodos method") {
            //  ARRANGE
            whenever(todoService.getAllTodos()).thenReturn(emptyList())

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
            whenever(todoService.getAllTodosByCompleted(any())).thenReturn(emptyList())

            //  ACT & ASSERT
            mvc.perform(get("/api/todos?completed=true"))
                .andExpect(status().isOk)

            //  VERIFY
            verify(todoService).getAllTodosByCompleted(any())
        }
    }
}
