package com.example.catsbreed.domain.model

data class Breed(
    val id: String,
    val name: String,
    val origin: String,
    val temperament: String,
    val description: String,
    val lifeSpan: String,
    val imageUrl: String?,
    val isFavourite: Boolean = false
) {

    /**
     * I am using this for the favourites average-lifespan summary
     */
    val lifeSpanLowerYears: Int
        get() = lifeSpan.substringBefore("-").trim().toIntOrNull() ?: 0
}