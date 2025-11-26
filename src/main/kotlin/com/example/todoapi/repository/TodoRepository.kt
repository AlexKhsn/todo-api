package com.example.todoapi.repository

import com.example.todoapi.entity.Priority
import com.example.todoapi.entity.Todo
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface TodoRepository : JpaRepository<Todo, Long> {
    @Query(
        "SELECT t FROM Todo t WHERE " +
            "(:completed IS NULL OR t.completed = :completed) AND " +
            "(:subtitle IS NULL OR :subtitle = '' OR LOWER(t.title) LIKE LOWER(CONCAT('%', :subtitle, '%'))) AND " +
            "(:priority IS NULL OR t.priority = :priority)",
    )
    fun findWithFilters(
        @Param("completed") completed: Boolean?,
        @Param("subtitle") subtitle: String?,
        @Param("priority") priority: Priority?,
        pageable: Pageable = Pageable.unpaged(),
    ): Page<Todo>
}
