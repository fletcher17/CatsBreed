package com.example.catsbreed.presentation.breedlist

import app.cash.turbine.test
import com.example.catsbreed.domain.usecase.LoadBreedsPageUseCase
import com.example.catsbreed.domain.usecase.ObserveBreedsUseCase
import com.example.catsbreed.domain.usecase.SearchBreedsUseCase
import com.example.catsbreed.domain.usecase.ToggleFavouriteUseCase
import com.example.catsbreed.fake.FakeBreedRepository
import com.example.catsbreed.util.MainDispatcherRule
import com.example.catsbreed.util.testBreed
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class BreedListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeBreedRepository()

    private fun buildViewModel(): BreedListViewModel {
        return BreedListViewModel(
            observeBreeds = ObserveBreedsUseCase(repository),
            loadBreedsPage = LoadBreedsPageUseCase(repository),
            searchBreeds = SearchBreedsUseCase(repository),
            toggleFavourite = ToggleFavouriteUseCase(repository)
        )
    }

    @Test
    fun `loads first page on init and exposes breeds`() = runTest {
        repository.pagesToReturn = listOf(listOf(testBreed(id = "1"), testBreed(id = "2")))
        val viewModel = buildViewModel()

        viewModel.uiState.test {
            var state = awaitItem()
            while (state.isInitialLoading || state.breeds.isEmpty()) {
                state = awaitItem()
            }
            assertThat(state.breeds).hasSize(2)
            assertThat(state.isInitialLoading).isFalse()
            assertThat(state.errorMessage).isNull()
        }
    }

    @Test
    fun `surfaces a friendly error when the first page fails and list stays empty`() = runTest {
        repository.loadPageError = IOException("boom")
        val viewModel = buildViewModel()

        viewModel.uiState.test {
            var state = awaitItem()
            while (state.isInitialLoading) state = awaitItem()
            assertThat(state.errorMessage).isNotNull()
            assertThat(state.breeds).isEmpty()
        }
    }

    @Test
    fun `toggling favourite delegates to the repository and reflects in state`() = runTest {
        repository.pagesToReturn = listOf(listOf(testBreed(id = "1", isFavourite = false)))
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.onToggleFavourite("1")
        advanceUntilIdle()

        assertThat(repository.catalogue.value.first { it.id == "1" }.isFavourite).isTrue()
    }

    @Test
    fun `blank search query clears search results and restores catalogue`() = runTest {
        repository.pagesToReturn = listOf(listOf(testBreed(id = "1")))
        val viewModel = buildViewModel()
        val collectJob = launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        viewModel.onSearchQueryChanged("")
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.searchQuery).isEmpty()
        assertThat(viewModel.uiState.value.breeds).hasSize(1)

        collectJob.cancel()
    }
}