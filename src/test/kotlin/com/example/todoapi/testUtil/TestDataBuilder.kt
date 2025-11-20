package com.example.todoapi.testUtil

import com.example.todoapi.dto.CreateTodoRequest
import com.example.todoapi.entity.Priority
import com.example.todoapi.entity.Todo
import java.time.LocalDateTime

object TestDataBuilder {
    fun requestFilled(
        title: String = "test",
        description: String? = "test",
        completed: Boolean? = true,
        priority: Priority? = Priority.HIGH,
    ) = CreateTodoRequest(title, description, completed, priority)

    fun requestDefault(
        title: String = "test",
        description: String? = null,
        completed: Boolean? = null,
        priority: Priority? = null,
    ) = CreateTodoRequest(title, description, completed, priority)

    fun entityToSaveFilled(
        id: Long? = null,
        title: String = "test",
        description: String? = "test",
        completed: Boolean = true,
        priority: Priority = Priority.HIGH,
        createdAt: LocalDateTime = LocalDateTime.now(),
        updatedAt: LocalDateTime = createdAt,
    ) = Todo(id, title, description, completed, priority, createdAt, updatedAt)

    fun entityToSaveDefault(
        id: Long? = null,
        title: String = "test",
        description: String? = null,
        completed: Boolean = false,
        priority: Priority = Priority.MEDIUM,
        createdAt: LocalDateTime = LocalDateTime.now(),
        updatedAt: LocalDateTime = createdAt,
    ) = Todo(id, title, description, completed, priority, createdAt, updatedAt)

    fun entitySavedFilled(
        id: Long? = 1L,
        title: String = "test",
        description: String? = "test",
        completed: Boolean = true,
        priority: Priority = Priority.HIGH,
        createdAt: LocalDateTime = LocalDateTime.now(),
        updatedAt: LocalDateTime = createdAt,
    ) = Todo(id, title, description, completed, priority, createdAt, updatedAt)

    fun entitySavedDefault(
        id: Long? = 1L,
        title: String = "test",
        description: String? = null,
        completed: Boolean = false,
        priority: Priority = Priority.MEDIUM,
        createdAt: LocalDateTime = LocalDateTime.now(),
        updatedAt: LocalDateTime = createdAt,
    ) = Todo(id, title, description, completed, priority, createdAt, updatedAt)

    fun listOfEntities(): List<Todo> {
        return listOf(
            entitySavedFilled(),
            entitySavedFilled(id = 2L),
            entitySavedDefault(id = 3L),
            entitySavedDefault(id = 4L),
        )
    }

    val longText =
        """
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        v
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl11
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        shjjkhfdulsdhfosdjflsjdflkjsdlkfjsldkjflksdjflkjsdlkfjlksdjflksjdlkfjlksdjflkjsdfl
        """.trimIndent()
}
