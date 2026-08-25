package com.example.catsbreed.presentation.detail

import com.example.catsbreed.domain.usecase.ObserveBreedDetailUseCase
import com.example.catsbreed.domain.usecase.RefreshBreedDetailUseCase
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

class BreedDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeBreedRepository()

    private fun buildViewModel(breedId: String = "1") = BreedDetailViewModel(
        breedId = breedId,
        observeBreedDetail = ObserveBreedDetailUseCase(repository),
        refreshBreedDetail = RefreshBreedDetailUseCase(repository),
        toggleFavourite = ToggleFavouriteUseCase(repository)
    )

    @Test
    fun `shows the cached breed as Success even while refresh is in flight`() = runTest {
        repository.pagesToReturn = listOf(listOf(testBreed(id = "1")))
        repository.loadBreedsPage(0, 20)

        val viewModel = buildViewModel("1")
        val collectJob = launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(BreedDetailUiState.Success::class.java)
        assertThat((state as BreedDetailUiState.Success).breed.id).isEqualTo("1")

        collectJob.cancel()
    }

    @Test
    fun `shows Error when nothing is cached and refresh fails`() = runTest {
        repository.refreshDetailError = IOException("offline")
        val viewModel = buildViewModel("missing")
        val collectJob = launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        assertThat(viewModel.uiState.value).isInstanceOf(BreedDetailUiState.Error::class.java)

        collectJob.cancel()

    }

    @Test
    fun `toggling favourite delegates to the repository`() = runTest {
        repository.pagesToReturn = listOf(listOf(testBreed(id = "1", isFavourite = false)))
        repository.loadBreedsPage(0, 20)
        val viewModel = buildViewModel("1")
        advanceUntilIdle()

        viewModel.onToggleFavourite()
        advanceUntilIdle()

        assertThat(repository.catalogue.value.first { it.id == "1" }.isFavourite).isTrue()
    }
}