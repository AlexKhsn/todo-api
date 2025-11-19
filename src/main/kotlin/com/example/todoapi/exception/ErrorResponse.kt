package com.example.todoapi.exception

import java.time.LocalDateTime

class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val path: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
)
