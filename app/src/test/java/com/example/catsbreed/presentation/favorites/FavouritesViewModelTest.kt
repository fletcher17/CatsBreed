package com.example.catsbreed.presentation.favorites

import app.cash.turbine.test
import com.example.catsbreed.domain.usecase.CalculateAverageLifespanUseCase
import com.example.catsbreed.domain.usecase.ObserveFavouritesUseCase
import com.example.catsbreed.fake.FakeBreedRepository
import com.example.catsbreed.presentation.favourites.FavouritesViewModel
import com.example.catsbreed.util.MainDispatcherRule
import com.example.catsbreed.util.testBreed
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class FavouritesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeBreedRepository()

    private fun buildViewModel() = FavouritesViewModel(
        observeFavourites = ObserveFavouritesUseCase(repository),
        calculateAverageLifespan = CalculateAverageLifespanUseCase()
    )

    @Test
    fun `emits empty state when there are no favourites`() = runTest {
        val viewModel = buildViewModel()
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.favourites).isEmpty()
            assertThat(state.averageLifespanYears).isEqualTo(0.0)
        }
    }

    @Test
    fun `computes average lifespan from favourites`() = runTest {
        repository.favourites.value = listOf(
            testBreed(id = "1", lifeSpan = "10 - 14", isFavourite = true),
            testBreed(id = "2", lifeSpan = "14 - 18", isFavourite = true)
        )
        val viewModel = buildViewModel()

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.favourites).hasSize(2)
            assertThat(state.averageLifespanYears).isEqualTo(12.0)
        }
    }

    @Test
    fun `reacts to new favourites added after initial emission`() = runTest {
        val viewModel = buildViewModel()
        viewModel.uiState.test {
            assertThat(awaitItem().favourites).isEmpty()
            repository.favourites.value = listOf(testBreed(id = "1", lifeSpan = "8 - 10", isFavourite = true))
            val updated = awaitItem()
            assertThat(updated.favourites).hasSize(1)
            assertThat(updated.averageLifespanYears).isEqualTo(8.0)
        }
    }
}