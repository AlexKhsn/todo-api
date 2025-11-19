package com.example.todoapi.exception

class CustomExceptions {
    class TodoNotFoundException(todoId: Long) :
        NoSuchElementException() {
        override val message = "Todo with id: $todoId not found"
        val customError = "Todo Not Found"
    }
}
