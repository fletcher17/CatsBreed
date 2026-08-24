package com.example.catsbreed.domain.repository

import com.example.catsbreed.domain.model.Breed
import kotlinx.coroutines.flow.Flow

interface BreedRepository {

    fun observeBreeds(): Flow<List<Breed>>

    suspend fun loadBreedsPage(page: Int, pageSize: Int): Result<Int>

    /** Remote search by name; falls back to filtering the local cache when offline. */
    suspend fun searchBreeds(query: String): Result<List<Breed>>

    /** Favourited breeds, sourced from the local database, updated reactively. */
    fun observeFavourites(): Flow<List<Breed>>

    /** Single breed, offline-first: emits the cached value immediately and refreshes from network. */
    fun observeBreed(breedId: String): Flow<Breed?>

    suspend fun refreshBreedDetail(breedId: String): Result<Unit>

    suspend fun toggleFavourite(breedId: String)
}