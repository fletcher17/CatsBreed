package com.example.catsbreed.data.mapper

import com.example.catsbreed.data.local.BreedEntity
import com.example.catsbreed.data.remote.dto.BreedDto
import com.example.catsbreed.domain.model.Breed

fun BreedDto.toEntity(sortIndex: Int? = null, isFavourite: Boolean = false): BreedEntity = BreedEntity(
    id = id,
    name = name,
    origin = origin,
    temperament = temperament,
    description = description,
    lifeSpan = lifeSpan,
    imageUrl = image?.url,
    isFavourite = isFavourite,
    sortIndex = sortIndex
)

fun BreedEntity.toDomain(): Breed = Breed(
    id = id,
    name = name,
    origin = origin,
    temperament = temperament,
    description = description,
    lifeSpan = lifeSpan,
    imageUrl = imageUrl,
    isFavourite = isFavourite
)