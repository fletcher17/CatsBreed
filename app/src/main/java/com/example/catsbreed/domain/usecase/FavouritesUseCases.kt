package com.example.catsbreed.domain.usecase

import com.example.catsbreed.domain.repository.BreedRepository

class ToggleFavouriteUseCase(private val repository: BreedRepository) {
    suspend operator fun invoke(breedId: String) = repository.toggleFavourite(breedId)
}