package com.example.catsbreed.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catsbreed.domain.usecase.ObserveBreedDetailUseCase
import com.example.catsbreed.domain.usecase.RefreshBreedDetailUseCase
import com.example.catsbreed.domain.usecase.ToggleFavouriteUseCase
import com.example.catsbreed.util.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BreedDetailViewModel(
    private val breedId: String,
    observeBreedDetail: ObserveBreedDetailUseCase,
    private val refreshBreedDetail: RefreshBreedDetailUseCase,
    private val toggleFavourite: ToggleFavouriteUseCase
): ViewModel() {

    private val errorMessage = MutableStateFlow<String?>(null)
    private val isRefreshing = MutableStateFlow(true)

    val uiState: StateFlow<BreedDetailUiState> = combine(
        observeBreedDetail(breedId), errorMessage, isRefreshing
    ) { breed, error, refreshing ->
        when {
            breed != null -> BreedDetailUiState.Success(breed, isRefreshing = refreshing)
            error != null -> BreedDetailUiState.Error(error)
            else -> BreedDetailUiState.Loading
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BreedDetailUiState.Loading)

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            isRefreshing.value = true
            refreshBreedDetail(breedId)
                .onFailure { errorMessage.value = it.toUserMessage() }
                .onSuccess { errorMessage.value = null }
            isRefreshing.value = false
        }
    }

    fun onToggleFavourite() {
        viewModelScope.launch { toggleFavourite(breedId) }
    }
}