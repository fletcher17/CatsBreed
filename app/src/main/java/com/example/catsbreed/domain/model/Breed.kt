package com.example.catsbreed.domain.model

/**
 * This is the core domai model. I intentionally  made free of network or Persistence (DTO or Entity) anotations so the
 * presentation layers doesn't depend on data-layer implementation details
 */
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