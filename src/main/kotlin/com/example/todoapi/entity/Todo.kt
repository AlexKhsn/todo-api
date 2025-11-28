package com.example.todoapi.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "todos")
class Todo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(nullable = false, length = 100)
    var title: String,
    @Column(length = 500)
    var description: String? = null,
    @Column(nullable = false)
    var completed: Boolean = false,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var priority: Priority = Priority.MEDIUM,
    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(nullable = false)
    var updatedAt: LocalDateTime = createdAt,
    @ManyToMany
    @JoinTable(name = "todo_tag")
    val tags: MutableSet<Tag> = mutableSetOf(),
)
