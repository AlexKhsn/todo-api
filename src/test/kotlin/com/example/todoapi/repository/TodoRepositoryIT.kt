package com.example.todoapi.repository

import com.example.todoapi.testUtil.TestDataBuilder
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.domain.PageRequest

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
        val result = repository.findWithFilters(true, null)

        //  ASSERT
        result.content.size shouldBe 2
        result.content.all { it.completed } shouldBe true
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
        val result = repository.findWithFilters(false, null)

        //  ASSERT
        result.content.size shouldBe 1
        result.content.all { it.completed } shouldBe false
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
        val result = repository.findWithFilters(true, null)

        //  ASSERT
        result.content.isEmpty() shouldBe true
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
        val result = repository.findWithFilters(false, null)

        //  ASSERT
        result.content.isEmpty() shouldBe true
    }

    @Test
    fun `Should return a list of entities contain subtitle in titles`() {
        //  ARRANGE
        val firstEntity = TestDataBuilder.entityToSaveFilled()
        val secondEntity = TestDataBuilder.entityToSaveFilled()
        val thirdEntity = TestDataBuilder.entityToSaveDefault(title = "default")

        entityManager.persist(firstEntity)
        entityManager.persist(secondEntity)
        entityManager.persist(thirdEntity)
        entityManager.flush()

        //  ACT
        val result = repository.findWithFilters(null, "tESt")

        //  ASSERT
        result.content.size shouldBe 2
        result.content.all { it.title.contains("tESt".lowercase()) } shouldBe true
    }

    @Test
    fun `Should return an empty list when no entities contain subtitle in titles`() {
        //  ARRANGE
        val firstEntity = TestDataBuilder.entityToSaveDefault()
        val secondEntity = TestDataBuilder.entityToSaveDefault()

        entityManager.persist(firstEntity)
        entityManager.persist(secondEntity)
        entityManager.flush()

        //  ACT
        val result = repository.findWithFilters(null, "default")

        //  ASSERT
        result.content.isEmpty() shouldBe true
    }

    @Test
    fun `Should return a list of completed entities contain subtitle in titles`() {
        //  ARRANGE
        val firstEntity = TestDataBuilder.entityToSaveFilled()
        val secondEntity = TestDataBuilder.entityToSaveFilled()
        val thirdEntity = TestDataBuilder.entityToSaveFilled(title = "default")

        entityManager.persist(firstEntity)
        entityManager.persist(secondEntity)
        entityManager.persist(thirdEntity)
        entityManager.flush()

        //  ACT
        val result = repository.findWithFilters(true, "tESt")

        //  ASSERT
        result.content.size shouldBe 2
        result.content.all { it.completed } shouldBe true
        result.content.all { it.title.contains("tESt".lowercase()) } shouldBe true
    }

    @Test
    fun `Should return an empty list when no completed entities contain subtitle in titles`() {
        //  ARRANGE
        val firstEntity = TestDataBuilder.entityToSaveDefault()
        val secondEntity = TestDataBuilder.entityToSaveDefault()

        entityManager.persist(firstEntity)
        entityManager.persist(secondEntity)
        entityManager.flush()

        //  ACT
        val result = repository.findWithFilters(true, "default")

        //  ASSERT
        result.content.isEmpty() shouldBe true
    }

    @Test
    fun `Should return correct page when requesting second page`() {
        //  ARRANGE
        val entities =
            (1..5).map { i ->
                TestDataBuilder.entityToSaveDefault(title = "Task $i")
            }
        entities.forEach { entityManager.persist(it) }
        entityManager.flush()
        val pageable = PageRequest.of(1, 2)

        //  ACT
        val result = repository.findWithFilters(null, null, pageable)

        //  ASSERT
        result.content.size shouldBe 2
        result.totalElements shouldBe entities.size
        result.totalPages shouldBe 3
        result.number shouldBe 1
        result.isFirst shouldBe false
        result.isLast shouldBe false
        result.content[0].title shouldBe entities[2].title
        result.content[1].title shouldBe entities[3].title
    }

    @Test
    fun `Should return last page with remaining elements`() {
        //  ARRANGE
        val entities =
            (1..5).map { i ->
                TestDataBuilder.entityToSaveDefault(title = "Task $i")
            }
        entities.forEach { entityManager.persist(it) }
        entityManager.flush()
        val pageable = PageRequest.of(2, 2)

        //  ACT
        val result = repository.findWithFilters(null, null, pageable)

        //  ASSERT
        result.content.size shouldBe 1
        result.number shouldBe 2
        result.isLast shouldBe true
        result.content[0].title shouldBe entities[4].title
    }
}
