package com.example.catsbreed.presentation.detail

import com.example.catsbreed.domain.model.Breed

sealed interface BreedDetailUiState {
    data object Loading : BreedDetailUiState
    data class Success(val breed: Breed, val isRefreshing: Boolean = false) : BreedDetailUiState
    data class Error(val message: String) : BreedDetailUiState
}