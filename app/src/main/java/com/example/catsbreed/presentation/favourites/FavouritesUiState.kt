package com.example.catsbreed.presentation.favourites

import com.example.catsbreed.domain.model.Breed

data class FavouritesUiState(
    val favourites: List<Breed> = emptyList(),
    val averageLifespanYears: Double = 0.0,
    val isLoading: Boolean = true
) {
    val isEmpty: Boolean get() = !isLoading && favourites.isEmpty()
}