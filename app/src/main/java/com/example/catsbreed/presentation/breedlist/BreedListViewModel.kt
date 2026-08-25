package com.example.catsbreed.presentation.breedlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.catsbreed.data.repository.BreedRepositoryImpl
import com.example.catsbreed.domain.model.Breed
import com.example.catsbreed.domain.usecase.LoadBreedsPageUseCase
import com.example.catsbreed.domain.usecase.ObserveBreedsUseCase
import com.example.catsbreed.domain.usecase.SearchBreedsUseCase
import com.example.catsbreed.domain.usecase.ToggleFavouriteUseCase
import com.example.catsbreed.util.NoConnectivityException
import com.example.catsbreed.util.toUserMessage
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.compareTo

private const val PAGE_SIZE = 20
private const val SEARCH_DEBOUNCE_MS = 350L


@OptIn(FlowPreview::class)
class BreedListViewModel(
    private val loadBreedsPage: LoadBreedsPageUseCase,
    private val observeBreeds: ObserveBreedsUseCase,
    private val searchBreeds: SearchBreedsUseCase,
    private val toggleFavourite: ToggleFavouriteUseCase
) : ViewModel() {

    private var currentPage = 0
    private val searchQuery = MutableStateFlow("")
    private val searchResults = MutableStateFlow<List<Breed>?>(null)
    private val loadingState = MutableStateFlow(LoadingState())

    private data class LoadingState(
        val isInitialLoading: Boolean = false,
        val isLoadingMore: Boolean = false,
        val isSearching: Boolean = false,
        val errorMessage: String? = null,
        val endReached: Boolean = false
    )

    val uiState: StateFlow<BreedListUiState> = combine(
        observeBreeds(), searchResults, searchQuery, loadingState
    ) { catalogue, search, query, loading ->
        BreedListUiState(
            breeds = search ?: catalogue,
            isInitialLoading = loading.isInitialLoading,
            isLoadingMore = loading.isLoadingMore,
            isSearching = loading.isSearching,
            searchQuery = query,
            errorMessage = loading.errorMessage,
            endReached = loading.endReached || search != null
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BreedListUiState())

    init {
        loadNextPage()
        searchQuery
            .drop(1)
            .debounce(SEARCH_DEBOUNCE_MS)
            .distinctUntilChanged()
            .onEach { query -> runSearch(query) }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
        if (query.isBlank()) searchResults.value = null
    }

    private fun runSearch(query: String) {
        if (query.isBlank()) {
            searchResults.value = null
            return
        }
        viewModelScope.launch {
            loadingState.value = loadingState.value.copy(isSearching = true, errorMessage = null)
            searchBreeds(query)
                .onSuccess { results ->
                    searchResults.value = results
                    loadingState.value = loadingState.value.copy(isSearching = false)
                }
                .onFailure { error ->
                    loadingState.value = loadingState.value.copy(
                        isSearching = false,
                        errorMessage = error.toUserMessage()
                    )
                }
        }
    }


    fun loadNextPage() {
        val state = loadingState.value
        if (state.isInitialLoading || state.isLoadingMore || state.endReached) return
        val isFirstPage = currentPage == 0

        viewModelScope.launch {
            loadingState.value = state.copy(
                isInitialLoading = isFirstPage,
                isLoadingMore = !isFirstPage,
                errorMessage = null
            )
            loadBreedsPage(page = currentPage, pageSize = PAGE_SIZE)
                .onSuccess { fetchedCount ->
                    currentPage++
                    loadingState.value = loadingState.value.copy(
                        isInitialLoading = false,
                        isLoadingMore = false,
                        endReached = fetchedCount < PAGE_SIZE
                    )
                }
                .onFailure { error ->
                    loadingState.value = loadingState.value.copy(
                        isInitialLoading = false,
                        isLoadingMore = false,
                        errorMessage = error.toUserMessage()
                    )
                }
        }
    }


    fun retry() {
        loadingState.value = loadingState.value.copy(errorMessage = null)
        if (searchQuery.value.isNotBlank()) runSearch(searchQuery.value) else loadNextPage()
    }

    fun onToggleFavourite(breedId: String) {
        viewModelScope.launch { toggleFavourite(breedId) }
    }
}