package com.example.catsbreed.presentation.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catsbreed.domain.usecase.CalculateAverageLifespanUseCase
import com.example.catsbreed.domain.usecase.ObserveFavouritesUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class FavouritesViewModel(
    observeFavourites: ObserveFavouritesUseCase,
    private val calculateAverageLifespan: CalculateAverageLifespanUseCase
) : ViewModel() {

    val uiState: StateFlow<FavouritesUiState> = observeFavourites()
        .map { favourites ->
            FavouritesUiState(
                favourites = favourites,
                averageLifespanYears = calculateAverageLifespan(favourites),
                isLoading = false
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FavouritesUiState())
}