package com.example.catsbreed.domain.usecase

import com.example.catsbreed.domain.model.Breed
import com.example.catsbreed.domain.repository.BreedRepository
import kotlinx.coroutines.flow.Flow
import kotlin.math.roundToInt

class ToggleFavouriteUseCase(private val repository: BreedRepository) {
    suspend operator fun invoke(breedId: String) = repository.toggleFavourite(breedId)
}

class ObserveFavouritesUseCase(private val repository: BreedRepository) {
    operator fun invoke(): Flow<List<Breed>> = repository.observeFavourites()
}

/**
 * I made this Pure function to isolate from Flow/coroutines so it's trivial to unit test with plain lists.
 * It Uses the lower bound of each breed's life-span range.
 */
class CalculateAverageLifespanUseCase {
    operator fun invoke(breeds: List<Breed>): Double {
        if (breeds.isEmpty()) return 0.0
        val avg = breeds.map { it.lifeSpanLowerYears }.average()
        return (avg * 10.0).roundToInt() / 10.0
    }
}