package com.example.todoapi.repository

import com.example.todoapi.testUtil.TestDataBuilder
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class TodoRepositoryIT {
    @Autowired
    lateinit var repository: TodoRepository

    @Autowired
    lateinit var entityManager: TestEntityManager

    @Test
    fun `Should return a list of existing completed entities`() {
        //  ARRANGE
        val firstEntity = TestDataBuilder.entityToSaveFilled()
        val secondEntity = TestDataBuilder.entityToSaveFilled()
        val thirdEntity = TestDataBuilder.entityToSaveDefault()

        entityManager.persist(firstEntity)
        entityManager.persist(secondEntity)
        entityManager.persist(thirdEntity)
        entityManager.flush()

        //  ACT
        val result = repository.findByCompleted(true)

        //  ASSERT
        result.size shouldBe 2
        result.all { it.completed } shouldBe true
    }

    @Test
    fun `Should return a list of existing non-completed entities`() {
        //  ARRANGE
        val firstEntity = TestDataBuilder.entityToSaveFilled()
        val secondEntity = TestDataBuilder.entityToSaveFilled()
        val thirdEntity = TestDataBuilder.entityToSaveDefault()

        entityManager.persist(firstEntity)
        entityManager.persist(secondEntity)
        entityManager.persist(thirdEntity)
        entityManager.flush()

        //  ACT
        val result = repository.findByCompleted(false)

        //  ASSERT
        result.size shouldBe 1
        result.all { it.completed } shouldBe false
    }

    @Test
    fun `Should return an empty list when completed entities don't exist`() {
        //  ARRANGE
        val firstEntity = TestDataBuilder.entityToSaveDefault()
        val secondEntity = TestDataBuilder.entityToSaveDefault()

        entityManager.persist(firstEntity)
        entityManager.persist(secondEntity)
        entityManager.flush()

        //  ACT
        val result = repository.findByCompleted(true)

        //  ASSERT
        result.isEmpty() shouldBe true
    }

    @Test
    fun `Should return an empty list when non-completed entities don't exist`() {
        //  ARRANGE
        val firstEntity = TestDataBuilder.entityToSaveFilled()
        val secondEntity = TestDataBuilder.entityToSaveFilled()

        entityManager.persist(firstEntity)
        entityManager.persist(secondEntity)
        entityManager.flush()

        //  ACT
        val result = repository.findByCompleted(false)

        //  ASSERT
        result.isEmpty() shouldBe true
    }
}
