package com.example.catsbreed.domain.usecase

import com.example.catsbreed.domain.model.Breed
import com.example.catsbreed.domain.repository.BreedRepository
import kotlinx.coroutines.flow.Flow

class LoadBreedsPageUseCase(private val repository: BreedRepository) {
    suspend operator fun invoke(page: Int, pageSize: Int = 20): Result<Int> =
        repository.loadBreedsPage(page, pageSize)
}

class ObserveBreedsUseCase(private val repository: BreedRepository) {
    operator fun invoke(): Flow<List<Breed>> = repository.observeBreeds()
}

class SearchBreedsUseCase(private val repository: BreedRepository) {
    suspend operator fun invoke(query: String): Result<List<Breed>> =
        repository.searchBreeds(query)
}