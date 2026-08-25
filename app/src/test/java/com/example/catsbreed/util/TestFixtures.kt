package com.example.catsbreed.util

import com.example.catsbreed.domain.model.Breed

fun testBreed(
    id: String = "abys",
    name: String = "Abyssinian",
    lifeSpan: String = "14 - 15",
    isFavourite: Boolean = false
) = Breed(
    id = id,
    name = name,
    origin = "Egypt",
    temperament = "Active, Energetic",
    description = "A short-haired breed.",
    lifeSpan = lifeSpan,
    imageUrl = "https://example.com/$id.jpg",
    isFavourite = isFavourite
)