package com.example.catsbreed.domain.usecase

import com.example.catsbreed.domain.model.Breed
import com.example.catsbreed.domain.repository.BreedRepository
import kotlinx.coroutines.flow.Flow

class ObserveBreedDetailUseCase(private val repository: BreedRepository) {
    operator fun invoke(breedId: String): Flow<Breed?> = repository.observeBreed(breedId)
}

class RefreshBreedDetailUseCase(private val repository: BreedRepository) {
    suspend operator fun invoke(breedId: String): Result<Unit> = repository.refreshBreedDetail(breedId)
}