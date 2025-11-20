package com.example.todoapi.dto

import com.example.todoapi.testUtil.TestDataBuilder
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import jakarta.validation.Validation

class UpdateTodoRequestValidationTest : FunSpec({
    val validator = Validation.buildDefaultValidatorFactory().validator

    test("Should throw exception when title is blank") {
        //  ARRANGE
        val request = UpdateTodoRequest(title = "")

        //  ACT
        val violations = validator.validate(request)

        //  ASSERT
        violations.isNotEmpty()
        violations.size shouldBe 1
        violations.first().message shouldBe "Title size should be between 1 and 100"
    }

    test("Should throw exception when title is more than 100") {
        //  ARRANGE
        val request =
            UpdateTodoRequest(
                title = TestDataBuilder.longText,
            )

        //  ACT
        val violations = validator.validate(request)

        //  ASSERT
        violations.isNotEmpty()
        violations.size shouldBe 1
        violations.first().message shouldBe "Title size should be between 1 and 100"
    }

    test("Should throw exception when description is longer than 500") {
        //  ARRANGE
        val request =
            UpdateTodoRequest(
                title = "test",
                description = TestDataBuilder.longText,
            )

        //  ACT
        val violations = validator.validate(request)

        //  ASSERT
        violations.isNotEmpty()
        violations.size shouldBe 1
        violations.first().message shouldBe "Description max size should be 500"
    }

    test("Should throw exception when title and description are not valid") {
        //  ARRANGE
        val request =
            UpdateTodoRequest(
                title = "",
                description = TestDataBuilder.longText,
            )

        //  ACT
        val violations = validator.validate(request)

        //  ASSERT
        violations.isNotEmpty()
        violations.size shouldBe 2
        violations.any { it.propertyPath.toString() == "title" } shouldBe true
        violations.any { it.propertyPath.toString() == "description" } shouldBe true
        violations.find { it.message == "Title size should be between 1 and 100" } shouldNotBe null
        violations.find { it.message == "Description max size should be 500" } shouldNotBe null
    }

    test("Should validate successfully when all fields are null") {
        //  ARRANGE
        val request = UpdateTodoRequest()

        //  ACT
        val violations = validator.validate(request)

        //  ASSERT
        violations.size shouldBe 0
    }
})
