package com.example.todoapi.service

import com.example.todoapi.dto.CreateTagRequest
import com.example.todoapi.dto.toModel
import com.example.todoapi.models.TagModel
import com.example.todoapi.models.toEntity
import com.example.todoapi.models.toModel
import com.example.todoapi.repository.TagRepository
import org.springframework.stereotype.Service

@Service
class TagService(
    private val tagRepository: TagRepository,
) {
    fun createTag(request: CreateTagRequest): TagModel {
        val requestModel = request.toModel()
        val tagToSave = requestModel.toEntity()
        val savedTag = tagRepository.save(tagToSave)
        return savedTag.toModel()
    }

    fun getTagById(id: Long): TagModel {
        val foundedTag =
            tagRepository.findById(id)
                .orElseThrow { NoSuchElementException("No tag with id $id not found") }
        return foundedTag.toModel()
    }

    fun getAllTags(): List<TagModel> {
        val tagEntities = tagRepository.findAll()
        return tagEntities.map { it.toModel() }
    }

    fun deleteTagById(id: Long): TagModel {
        val tagToDelete = getTagById(id)
        tagRepository.deleteById(id)
        return tagToDelete
    }
}
