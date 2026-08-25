package com.example.catsbreed.presentation.breedlist

import com.example.catsbreed.domain.model.Breed

data class BreedListUiState(
    val breeds: List<Breed> = emptyList(),
    val isInitialLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isSearching: Boolean = false,
    val searchQuery: String = "",
    val errorMessage: String? = null,
    val endReached: Boolean = false
) {
    val isEmpty: Boolean get() = breeds.isEmpty() && !isInitialLoading && errorMessage == null
}