package com.example.todoapi.dto

import com.example.todoapi.testUtil.TestDataBuilder
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import jakarta.validation.Validation

class CreateTodoRequestValidationTest : FunSpec({
    val validator = Validation.buildDefaultValidatorFactory().validator

    test("Should throw exception when title is blank") {
        //  ARRANGE
        val request = TestDataBuilder.requestDefault(title = "")

        //  ACT
        val violations = validator.validate(request)

        //  ASSERT
        violations.isNotEmpty()
        violations.all { it.propertyPath.toString().contains("title") } shouldBe true
        violations.find { it.message.contains("Title must not be empty") } shouldNotBe null
        violations.find { it.message.contains("Title size should be between 1 and 100") } shouldNotBe null
    }

    test("Should throw exception when title is longer than 100") {
        //  ARRANGE
        val request =
            TestDataBuilder.requestDefault(
                title =
                    "skdjhfjksdhfkjsdhfjkshdjkfhskdjhfkjsdhfjkshdfjkhsdkjfh" +
                        "kjsdhfjksdhfksjdhfkjsdhfjkshdkjfhksjdhfjksdhfjhsdjfhkjsdhkfhjks" +
                        "hsjdbfhjsbdhfjbsdhjfbshjdbfjhsdbfhjsdbhjfbsdjhfbhjsdbfhjsdbfjhbshjd",
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
            TestDataBuilder.requestDefault(
                description =
                    "skdjhfjksdhfkjsdhfjkshdjkfhskdjhfkjsdhfjkshdfjkhsdkjfh" +
                        "kjsdhfjksdhfksjdhfkjsdhfjkshdkjfhksjdhfjksdhfjhsdjfhkjsdhkfhjks" +
                        "hsjdbfhjsbdhfjbsdhjfbshjdbfjhsdbfhjsdbhjfbsdjhfbhjsdbfhjsdbfjhbshjd" +
                        "kjsdhfjksdhfjkhsdkjfhskjdhfksdhfjksdhfliksdhfklsdhfjkshdkjfskdhfklsdhflk" +
                        "skdjhfksjdhfjksdjkfhsjkdhfkjshdkfjhskjdhfjksdkfjhskdjhfjksdhfjkshdkjfh" +
                        "skdjhfkjshdfjkhskdjhfkjsdhfkjgsdhjfgui4rghiuh8923yiruhdfhskjhdkfbnsd" +
                        "skdjhfjksdhfkjsdhfjkshdjkfhskdjhfkjsdhfjkshdfjkhsdkjfh" +
                        "kjsdhfjksdhfksjdhfkjsdhfjkshdkjfhksjdhfjksdhfjhsdjfhkjsdhkfhjks" +
                        "hsjdbfhjsbdhfjbsdhjfbshjdbfjhsdbfhjsdbhjfbsdjhfbhjsdbfhjsdbfjhbshjd" +
                        "kjsdhfjksdhfjkhsdkjfhskjdhfksdhfjksdhfliksdhfklsdhfjkshdkjfskdhfklsdhflk" +
                        "skdjhfksjdhfjksdjkfhsjkdhfkjshdkfjhskjdhfjksdkfjhskdjhfjksdhfjkshdkjfh" +
                        "skdjhfkjshdfjkhskdjhfkjsdhfkjgsdhjfgui4rghiuh8923yiruhdfhskjhdkfbnsd" +
                        "skdjhfjksdhfkjsdhfjkshdjkfhskdjhfkjsdhfjkshdfjkhsdkjfh" +
                        "kjsdhfjksdhfksjdhfkjsdhfjkshdkjfhksjdhfjksdhfjhsdjfhkjsdhkfhjks" +
                        "hsjdbfhjsbdhfjbsdhjfbshjdbfjhsdbfhjsdbhjfbsdjhfbhjsdbfhjsdbfjhbshjd" +
                        "kjsdhfjksdhfjkhsdkjfhskjdhfksdhfjksdhfliksdhfklsdhfjkshdkjfskdhfklsdhflk" +
                        "skdjhfksjdhfjksdjkfhsjkdhfkjshdkfjhskjdhfjksdkfjhskdjhfjksdhfjkshdkjfh" +
                        "skdjhfkjshdfjkhskdjhfkjsdhfkjgsdhjfgui4rghiuh8923yiruhdfhskjhdkfbnsd" +
                        "skdjhfjksdhfkjsdhfjkshdjkfhskdjhfkjsdhfjkshdfjkhsdkjfh" +
                        "kjsdhfjksdhfksjdhfkjsdhfjkshdkjfhksjdhfjksdhfjhsdjfhkjsdhkfhjks" +
                        "hsjdbfhjsbdhfjbsdhjfbshjdbfjhsdbfhjsdbhjfbsdjhfbhjsdbfhjsdbfjhbshjd" +
                        "kjsdhfjksdhfjkhsdkjfhskjdhfksdhfjksdhfliksdhfklsdhfjkshdkjfskdhfklsdhflk" +
                        "skdjhfksjdhfjksdjkfhsjkdhfkjshdkfjhskjdhfjksdkfjhskdjhfjksdhfjkshdkjfh" +
                        "skdjhfkjshdfjkhskdjhfkjsdhfkjgsdhjfgui4rghiuh8923yiruhdfhskjhdkfbnsd" +
                        "skdjhfjksdhfkjsdhfjkshdjkfhskdjhfkjsdhfjkshdfjkhsdkjfh" +
                        "kjsdhfjksdhfksjdhfkjsdhfjkshdkjfhksjdhfjksdhfjhsdjfhkjsdhkfhjks" +
                        "hsjdbfhjsbdhfjbsdhjfbshjdbfjhsdbfhjsdbhjfbsdjhfbhjsdbfhjsdbfjhbshjd" +
                        "kjsdhfjksdhfjkhsdkjfhskjdhfksdhfjksdhfliksdhfklsdhfjkshdkjfskdhfklsdhflk" +
                        "skdjhfksjdhfjksdjkfhsjkdhfkjshdkfjhskjdhfjksdkfjhskdjhfjksdhfjkshdkjfh" +
                        "skdjhfkjshdfjkhskdjhfkjsdhfkjgsdhjfgui4rghiuh8923yiruhdfhskjhdkfbnsd",
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
            TestDataBuilder.requestDefault(
                title = "",
                description =
                    "skdjhfjksdhfkjsdhfjkshdjkfhskdjhfkjsdhfjkshdfjkhsdkjfh" +
                        "kjsdhfjksdhfksjdhfkjsdhfjkshdkjfhksjdhfjksdhfjhsdjfhkjsdhkfhjks" +
                        "hsjdbfhjsbdhfjbsdhjfbshjdbfjhsdbfhjsdbhjfbsdjhfbhjsdbfhjsdbfjhbshjd" +
                        "kjsdhfjksdhfjkhsdkjfhskjdhfksdhfjksdhfliksdhfklsdhfjkshdkjfskdhfklsdhflk" +
                        "skdjhfksjdhfjksdjkfhsjkdhfkjshdkfjhskjdhfjksdkfjhskdjhfjksdhfjkshdkjfh" +
                        "skdjhfkjshdfjkhskdjhfkjsdhfkjgsdhjfgui4rghiuh8923yiruhdfhskjhdkfbnsd" +
                        "skdjhfjksdhfkjsdhfjkshdjkfhskdjhfkjsdhfjkshdfjkhsdkjfh" +
                        "kjsdhfjksdhfksjdhfkjsdhfjkshdkjfhksjdhfjksdhfjhsdjfhkjsdhkfhjks" +
                        "hsjdbfhjsbdhfjbsdhjfbshjdbfjhsdbfhjsdbhjfbsdjhfbhjsdbfhjsdbfjhbshjd" +
                        "kjsdhfjksdhfjkhsdkjfhskjdhfksdhfjksdhfliksdhfklsdhfjkshdkjfskdhfklsdhflk" +
                        "skdjhfksjdhfjksdjkfhsjkdhfkjshdkfjhskjdhfjksdkfjhskdjhfjksdhfjkshdkjfh" +
                        "skdjhfkjshdfjkhskdjhfkjsdhfkjgsdhjfgui4rghiuh8923yiruhdfhskjhdkfbnsd" +
                        "skdjhfjksdhfkjsdhfjkshdjkfhskdjhfkjsdhfjkshdfjkhsdkjfh" +
                        "kjsdhfjksdhfksjdhfkjsdhfjkshdkjfhksjdhfjksdhfjhsdjfhkjsdhkfhjks" +
                        "hsjdbfhjsbdhfjbsdhjfbshjdbfjhsdbfhjsdbhjfbsdjhfbhjsdbfhjsdbfjhbshjd" +
                        "kjsdhfjksdhfjkhsdkjfhskjdhfksdhfjksdhfliksdhfklsdhfjkshdkjfskdhfklsdhflk" +
                        "skdjhfksjdhfjksdjkfhsjkdhfkjshdkfjhskjdhfjksdkfjhskdjhfjksdhfjkshdkjfh" +
                        "skdjhfkjshdfjkhskdjhfkjsdhfkjgsdhjfgui4rghiuh8923yiruhdfhskjhdkfbnsd" +
                        "skdjhfjksdhfkjsdhfjkshdjkfhskdjhfkjsdhfjkshdfjkhsdkjfh" +
                        "kjsdhfjksdhfksjdhfkjsdhfjkshdkjfhksjdhfjksdhfjhsdjfhkjsdhkfhjks" +
                        "hsjdbfhjsbdhfjbsdhjfbshjdbfjhsdbfhjsdbhjfbsdjhfbhjsdbfhjsdbfjhbshjd" +
                        "kjsdhfjksdhfjkhsdkjfhskjdhfksdhfjksdhfliksdhfklsdhfjkshdkjfskdhfklsdhflk" +
                        "skdjhfksjdhfjksdjkfhsjkdhfkjshdkfjhskjdhfjksdkfjhskdjhfjksdhfjkshdkjfh" +
                        "skdjhfkjshdfjkhskdjhfkjsdhfkjgsdhjfgui4rghiuh8923yiruhdfhskjhdkfbnsd" +
                        "skdjhfjksdhfkjsdhfjkshdjkfhskdjhfkjsdhfjkshdfjkhsdkjfh" +
                        "kjsdhfjksdhfksjdhfkjsdhfjkshdkjfhksjdhfjksdhfjhsdjfhkjsdhkfhjks" +
                        "hsjdbfhjsbdhfjbsdhjfbshjdbfjhsdbfhjsdbhjfbsdjhfbhjsdbfhjsdbfjhbshjd" +
                        "kjsdhfjksdhfjkhsdkjfhskjdhfksdhfjksdhfliksdhfklsdhfjkshdkjfskdhfklsdhflk" +
                        "skdjhfksjdhfjksdjkfhsjkdhfkjshdkfjhskjdhfjksdkfjhskdjhfjksdhfjkshdkjfh" +
                        "skdjhfkjshdfjkhskdjhfkjsdhfkjgsdhjfgui4rghiuh8923yiruhdfhskjhdkfbnsd",
            )

        //  ACT
        val violations = validator.validate(request)

        //  ASSERT
        violations.isNotEmpty()
        violations.size shouldBe 3
        violations.any { it.propertyPath.toString() == "title" } shouldBe true
        violations.any { it.propertyPath.toString() == "description" } shouldBe true
        violations.find { it.message == "Title must not be empty!" } shouldNotBe null
        violations.find { it.message == "Title size should be between 1 and 100" } shouldNotBe null
        violations.find { it.message == "Description max size should be 500" } shouldNotBe null
    }

    test("Should validate successfully with fully filled request") {
        //  ARRANGE
        val request = TestDataBuilder.requestFilled()

        //  ACT
        val violations = validator.validate(request)

        //  ASSERT
        violations.isEmpty()
    }

    test("Should validate successfully with half-filled request") {
        //  ARRANGE
        val request = TestDataBuilder.requestDefault()

        //  ACT
        val violations = validator.validate(request)

        //  ASSERT
        violations.isEmpty()
    }
})
