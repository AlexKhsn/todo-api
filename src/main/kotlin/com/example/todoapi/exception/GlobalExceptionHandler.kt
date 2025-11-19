package com.example.todoapi.exception

import java.time.LocalDateTime
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFoundException(
        e: NoSuchElementException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        return createErrorResponse(
            status = HttpStatus.NOT_FOUND,
            message = e.message ?: "Resource not found",
            request = request,
        )
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(
        e: IllegalArgumentException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        return createErrorResponse(
            status = HttpStatus.BAD_REQUEST,
            message = e.message ?: "Invalid input data",
            request = request,
        )
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(
        e: HttpMessageNotReadableException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        return createErrorResponse(
            status = HttpStatus.BAD_REQUEST,
            message = e.message ?: "Invalid JSON format: ${e.message}",
            request = request,
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(
        e: MethodArgumentNotValidException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        return createErrorResponse(
            status = HttpStatus.BAD_REQUEST,
            message =
                e.bindingResult.fieldErrors
                    .joinToString("; ") { "${it.field}: ${it.defaultMessage}" },
            request = request,
        )
    }

    fun createErrorResponse(
        status: HttpStatus,
        message: String,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        val error =
            ErrorResponse(
                status = status.value(),
                error = status.reasonPhrase,
                message = message,
                path = request.getDescription(false).replace("uri=", ""),
                timestamp = LocalDateTime.now(),
            )

        return ResponseEntity.status(status).body(error)
    }
}
