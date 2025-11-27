package com.example.todoapi.repository

import com.example.todoapi.entity.Priority
import com.example.todoapi.testUtil.TestDataBuilder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

@DataJpaTest
class TodoRepositoryIT {
    @Autowired
    lateinit var repository: TodoRepository

    @Autowired
    lateinit var entityManager: TestEntityManager

    @BeforeEach
    fun cleanup() {
        repository.deleteAll()
        entityManager.flush()
        entityManager.clear()
        entityManager.entityManager
            .createNativeQuery("ALTER TABLE todos ALTER COLUMN id RESTART WITH 1")
            .executeUpdate()
    }

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
        val result = repository.findWithFilters(true, null, null)

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
        val result = repository.findWithFilters(false, null, null)

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
        val result = repository.findWithFilters(true, null, null)

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
        val result = repository.findWithFilters(false, null, null)

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
        val result = repository.findWithFilters(null, "tESt", null)

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
        val result = repository.findWithFilters(null, "default", null)

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
        val result = repository.findWithFilters(true, "tESt", null)

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
        val result = repository.findWithFilters(true, "default", null)

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
        val result = repository.findWithFilters(null, null, null, pageable)

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
        val result = repository.findWithFilters(null, null, null, pageable)

        //  ASSERT
        result.content.size shouldBe 1
        result.number shouldBe 2
        result.isLast shouldBe true
        result.content[0].title shouldBe entities[4].title
    }

    @Test
    fun `Should return todos sorted by title ascending`() {
        //  ARRANGE
        val entities =
            listOf(
                TestDataBuilder.entityToSaveDefault(title = "Zebra task"),
                TestDataBuilder.entityToSaveDefault(title = "Apple task"),
                TestDataBuilder.entityToSaveDefault(title = "Banana task"),
            )
        entities.forEach { entityManager.persist(it) }
        entityManager.flush()
        val pageable = PageRequest.of(0, 10, Sort.by("title").ascending())

        //  ACT
        val result = repository.findWithFilters(null, null, null, pageable)

        //  ASSERT
        result.content[0].title shouldBe "Apple task"
        result.content[1].title shouldBe "Banana task"
        result.content[2].title shouldBe "Zebra task"
    }

    @Test
    fun `Should handle multiple sort criteria`() {
        //  ARRANGE
        val entities =
            listOf(
                TestDataBuilder.entityToSaveDefault(title = "Zebra task", priority = Priority.LOW),
                TestDataBuilder.entityToSaveDefault(title = "Apple task", priority = Priority.HIGH),
                TestDataBuilder.entityToSaveDefault(title = "Banana task", priority = Priority.HIGH),
            )
        entities.forEach { entityManager.persist(it) }
        entityManager.flush()
        val pageable = PageRequest.of(0, 10, Sort.by("priority").ascending().and(Sort.by("title").ascending()))

        //  ACT
        val result = repository.findWithFilters(null, null, null, pageable)

        //  ASSERT
        result.content[0].title shouldBe "Apple task"
        result.content[0].priority shouldBe Priority.HIGH
        result.content[1].title shouldBe "Banana task"
        result.content[1].priority shouldBe Priority.HIGH
        result.content[2].title shouldBe "Zebra task"
        result.content[2].priority shouldBe Priority.LOW
    }

    @Test
    fun `Should apply sorting with filtering`() {
        //  ARRANGE
        val entities =
            listOf(
                TestDataBuilder.entityToSaveDefault(title = "Zebra task"),
                TestDataBuilder.entityToSaveDefault(title = "Apple task", completed = true),
                TestDataBuilder.entityToSaveDefault(title = "Banana task", completed = true),
            )
        entities.forEach { entityManager.persist(it) }
        entityManager.flush()
        val pageable = PageRequest.of(0, 10, Sort.by("title").ascending())

        //  ACT
        val result = repository.findWithFilters(true, null, null, pageable)

        //  ASSERT
        result.content.size shouldBe 2
        result.totalElements shouldBe 2
        result.content[0].title shouldBe "Apple task"
        result.content[0].completed shouldBe true
        result.content[1].title shouldBe "Banana task"
        result.content[1].completed shouldBe true
    }

    @Test
    fun `Should return only HIGH priority todos when filtering by HIGH`() {
        //  ARRANGE
        val entities =
            listOf(
                TestDataBuilder.entityToSaveDefault(title = "Zebra task", priority = Priority.HIGH),
                TestDataBuilder.entityToSaveDefault(title = "Apple task", completed = true),
                TestDataBuilder.entityToSaveDefault(title = "Banana task", completed = true, priority = Priority.HIGH),
            )
        entities.forEach { entityManager.persist(it) }
        entityManager.flush()
        val pageable = PageRequest.of(0, 10)

        //  ACT
        val result = repository.findWithFilters(null, null, Priority.HIGH, pageable)

        //  ASSERT
        result.content.size shouldBe 2
        result.totalElements shouldBe 2
        result.content.all { it.priority == Priority.HIGH } shouldBe true
    }

    @Test
    fun `Should return empty list when no todos match priority filter`() {
        //  ARRANGE
        val entities =
            listOf(
                TestDataBuilder.entityToSaveDefault(title = "Zebra task", priority = Priority.LOW),
                TestDataBuilder.entityToSaveDefault(title = "Apple task", completed = true),
                TestDataBuilder.entityToSaveDefault(title = "Banana task", completed = true, priority = Priority.LOW),
            )
        entities.forEach { entityManager.persist(it) }
        entityManager.flush()
        val pageable = PageRequest.of(0, 10)

        //  ACT
        val result = repository.findWithFilters(null, null, Priority.HIGH, pageable)

        //  ASSERT
        result.content.size shouldBe 0
        result.totalElements shouldBe 0
    }

    @Test
    fun `Should combine priority filter with completed filter`() {
        //  ARRANGE
        val entities =
            listOf(
                TestDataBuilder.entityToSaveDefault(title = "Zebra task", priority = Priority.HIGH),
                TestDataBuilder.entityToSaveDefault(title = "Apple task", completed = true, priority = Priority.HIGH),
                TestDataBuilder.entityToSaveDefault(title = "Banana task", completed = true, priority = Priority.HIGH),
            )
        entities.forEach { entityManager.persist(it) }
        entityManager.flush()
        val pageable = PageRequest.of(0, 10)

        //  ACT
        val result = repository.findWithFilters(true, null, Priority.HIGH, pageable)

        //  ASSERT
        result.content.size shouldBe 2
        result.totalElements shouldBe 2
        result.content.any { it -> it.title == "Zebra task" } shouldBe false
    }

    @Test
    fun `Should combine priority filter with subtitle search`() {
        //  ARRANGE
        val entities =
            listOf(
                TestDataBuilder.entityToSaveDefault(title = "Zebra", priority = Priority.HIGH),
                TestDataBuilder.entityToSaveDefault(title = "Apple task", priority = Priority.HIGH),
                TestDataBuilder.entityToSaveDefault(title = "Banana task", priority = Priority.HIGH),
            )
        entities.forEach { entityManager.persist(it) }
        entityManager.flush()
        val pageable = PageRequest.of(0, 10)

        //  ACT
        val result = repository.findWithFilters(null, "task", Priority.HIGH, pageable)

        //  ASSERT
        result.content.size shouldBe 2
        result.totalElements shouldBe 2
        result.content.any { it.title == "Zebra task" } shouldBe false
    }

    @Test
    fun `Should delete multiple todos by ids`() {
        //  ARRANGE
        val entities =
            listOf(
                TestDataBuilder.entityToSaveDefault(),
                TestDataBuilder.entityToSaveDefault(),
                TestDataBuilder.entityToSaveDefault(),
                TestDataBuilder.entityToSaveDefault(),
                TestDataBuilder.entityToSaveDefault(),
            )
        entities.forEach { entityManager.persist(it) }
        entityManager.flush()
        val idsToDelete = entities.take(3).map { it.id!! }

        //  ACT
        val result = repository.deleteByIdIn(idsToDelete)
        entityManager.flush() // Применить изменения

        //  ASSERT
        result shouldBe 3
        repository.count() shouldBe 2
    }

    @Test
    fun `Should delete nothing when ids not exist`() {
        //  ARRANGE
        val entities =
            listOf(
                TestDataBuilder.entityToSaveDefault(),
                TestDataBuilder.entityToSaveDefault(),
                TestDataBuilder.entityToSaveDefault(),
                TestDataBuilder.entityToSaveDefault(),
                TestDataBuilder.entityToSaveDefault(),
            )
        entities.forEach { entityManager.persist(it) }
        entityManager.flush()

        val nonExistingIds = listOf(12L, 23L, 43L)

        //  ACT
        val result = repository.deleteByIdIn(nonExistingIds)
        entityManager.flush() // Применить изменения

        //  ASSERT
        result shouldBe 0
        repository.count() shouldBe 5
    }

    @Test
    fun `Should update only completed field for multiple todos`() {
        //  ARRANGE
        val entities =
            listOf(
                TestDataBuilder.entityToSaveDefault(),
                TestDataBuilder.entityToSaveDefault(),
                TestDataBuilder.entityToSaveDefault(),
            )
        entities.forEach { entityManager.persist(it) }
        entityManager.flush()
        val ids = entities.map { it.id!! }

        //  ACT
        val result = repository.updateByIdIn(ids, true, null)

        //  ASSERT
        entityManager.flush()
        entityManager.clear()
        val updated = repository.findAllById(ids)

        result shouldBe 3
        updated.all { it.completed && it.priority == Priority.MEDIUM } shouldBe true
    }

    @Test
    fun `Should update only priority field for multiple todos`() {
        //  ARRANGE
        val entities =
            listOf(
                TestDataBuilder.entityToSaveDefault(),
                TestDataBuilder.entityToSaveDefault(),
                TestDataBuilder.entityToSaveDefault(),
            )
        entities.forEach { entityManager.persist(it) }
        entityManager.flush()
        val ids = entities.map { it.id!! }

        //  ACT
        val result = repository.updateByIdIn(ids, null, Priority.HIGH)

        //  ASSERT
        entityManager.flush()
        entityManager.clear()
        val updated = repository.findAllById(ids)

        result shouldBe 3
        updated.all { !it.completed && it.priority == Priority.HIGH } shouldBe true
    }

    @Test
    fun `Should update both completed and priority fields`() {
        //  ARRANGE
        val entities =
            listOf(
                TestDataBuilder.entityToSaveDefault(),
                TestDataBuilder.entityToSaveDefault(),
                TestDataBuilder.entityToSaveDefault(),
            )
        entities.forEach { entityManager.persist(it) }
        entityManager.flush()
        val ids = entities.map { it.id!! }

        //  ACT
        val result = repository.updateByIdIn(ids, true, Priority.HIGH)

        //  ASSERT
        entityManager.flush()
        entityManager.clear()
        val updated = repository.findAllById(ids)

        result shouldBe 3
        updated.all { it.completed && it.priority == Priority.HIGH } shouldBe true
    }

    @Test
    fun `Should update only specified todos by ids`() {
        //  ARRANGE
        val entities =
            listOf(
                TestDataBuilder.entityToSaveDefault(),
                TestDataBuilder.entityToSaveDefault(),
                TestDataBuilder.entityToSaveDefault(),
                TestDataBuilder.entityToSaveDefault(),
                TestDataBuilder.entityToSaveDefault(),
            )
        entities.forEach { entityManager.persist(it) }
        entityManager.flush()
        val ids = entities.take(3).map { it.id!! }

        //  ACT
        val result = repository.updateByIdIn(ids, true, null)

        //  ASSERT
        entityManager.flush()
        entityManager.clear()
        val refreshedEntities = repository.findAll()
        result shouldBe 3
        refreshedEntities.count { it.completed } shouldBe 3
        refreshedEntities.count { !it.completed } shouldBe 2
    }

    @Test
    fun `Should update updatedAt field when updating todos`() {
        //  ARRANGE
        val entities =
            listOf(
                TestDataBuilder.entityToSaveDefault(),
                TestDataBuilder.entityToSaveDefault(),
                TestDataBuilder.entityToSaveDefault(),
            )
        entities.forEach { entityManager.persist(it) }
        entityManager.flush()
        val savedEntities = repository.findAllById(entities.map { it.id!! })
        val ids = savedEntities.map { it.id!! }

        //  ACT
        val result = repository.updateByIdIn(ids, true, null)

        //  ASSERT
        entityManager.flush()
        entityManager.clear()
        val updated = repository.findAllById(ids)

        updated.forEachIndexed { index, todo ->
            todo.updatedAt.toString() shouldNotBe savedEntities[index].updatedAt.toString()
        }
    }

    @Test
    fun `Should not update when both parameters are null`() {
        //  ARRANGE
        val entities =
            listOf(
                TestDataBuilder.entityToSaveDefault(),
                TestDataBuilder.entityToSaveDefault(),
                TestDataBuilder.entityToSaveDefault(),
            )
        entities.forEach { entityManager.persist(it) }
        entityManager.flush()
        val savedEntities = repository.findAllById(entities.map { it.id!! })
        val ids = savedEntities.map { it.id!! }

        //  ACT
        val result = repository.updateByIdIn(ids, null, null)

        //  ASSERT
        entityManager.flush()
        entityManager.clear()
        val updated = repository.findAllById(ids)

        updated.forEachIndexed { index, todo ->
            todo.updatedAt.toString() shouldBe savedEntities[index].updatedAt.toString()
        }
    }
}
