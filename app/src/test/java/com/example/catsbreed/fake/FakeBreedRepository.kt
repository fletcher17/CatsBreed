package com.example.catsbreed.fake

import com.example.catsbreed.domain.model.Breed
import com.example.catsbreed.domain.repository.BreedRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeBreedRepository : BreedRepository {

    val catalogue = MutableStateFlow<List<Breed>>(emptyList())
    val favourites = MutableStateFlow<List<Breed>>(emptyList())
    private val breedById = MutableStateFlow<Map<String, Breed>>(emptyMap())

    var pagesToReturn: List<List<Breed>> = emptyList()
    var loadPageError: Throwable? = null
    var searchResult: Result<List<Breed>> = Result.success(emptyList())
    var refreshDetailError: Throwable? = null

    private var pageIndex = 0

    override fun observeBreeds(): StateFlow<List<Breed>> = catalogue

    override suspend fun loadBreedsPage(page: Int, pageSize: Int): Result<Int> {
        loadPageError?.let { return Result.failure(it) }
        val nextPage = pagesToReturn.getOrNull(pageIndex) ?: emptyList()
        pageIndex++
        catalogue.value = catalogue.value + nextPage
        nextPage.forEach { breedById.value = breedById.value + (it.id to it) }
        return Result.success(nextPage.size)
    }

    override suspend fun searchBreeds(query: String): Result<List<Breed>> = searchResult

    override fun observeFavourites(): StateFlow<List<Breed>> = favourites

    override fun observeBreed(breedId: String): kotlinx.coroutines.flow.Flow<Breed?> =
        breedById.let { flow -> kotlinx.coroutines.flow.MutableStateFlow(flow.value[breedId]) }

    override suspend fun refreshBreedDetail(breedId: String): Result<Unit> {
        refreshDetailError?.let { return Result.failure(it) }
        return Result.success(Unit)
    }

    override suspend fun toggleFavourite(breedId: String) {
        val breed = breedById.value[breedId] ?: catalogue.value.find { it.id == breedId } ?: return
        val updated = breed.copy(isFavourite = !breed.isFavourite)
        breedById.value = breedById.value + (breedId to updated)
        catalogue.value = catalogue.value.map { if (it.id == breedId) updated else it }
        favourites.value = if (updated.isFavourite) {
            favourites.value + updated
        } else {
            favourites.value.filterNot { it.id == breedId }
        }
    }
}