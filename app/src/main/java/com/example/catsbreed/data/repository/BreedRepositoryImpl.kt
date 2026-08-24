package com.example.catsbreed.data.repository

import com.example.catsbreed.data.local.BreedDao
import com.example.catsbreed.data.mapper.toDomain
import com.example.catsbreed.data.mapper.toEntity
import com.example.catsbreed.data.remote.CatApiService
import com.example.catsbreed.domain.model.Breed
import com.example.catsbreed.domain.repository.BreedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

class BreedRepositoryImpl(
    private val api: CatApiService,
    private val dao: BreedDao
) : BreedRepository {
    override fun observeBreeds(): Flow<List<Breed>> =
        dao.observeCatalogue().map { list -> list.map { it.toDomain() } }

    override suspend fun loadBreedsPage(
        page: Int,
        pageSize: Int
    ): Result<Int> = runCatching {
        val startIndex = (dao.getMaxSortIndex() ?: -1) + 1
        val remote = api.getBreeds(limit = pageSize, page = page)
        val entities = remote.mapIndexed { i, dto -> dto.toEntity(sortIndex = startIndex + i) }
        dao.upsertCataloguePage(entities)
        remote.size
    }.recoverCatching {
        throwable ->
        // Network failed - if we already have cached data the user can keep browsing offline,
        // so only propagate the error, never erase what's already in Room.
        throw mapError(throwable)
    }

    override suspend fun searchBreeds(query: String): Result<List<Breed>> {
        return runCatching {
            val remote = api.searchBreeds(query)
            val entities = remote.map { dto ->
                dto.toEntity(sortIndex = null, isFavourite = dao.isFavourite(dto.id) ?: false)
            }
            dao.insertOrReplaceAll(entities)
            entities.map { it.toDomain() }
        }.recoverCatching {
            // Offline fallback: filter whatever is already cached instead of failing outright.
            val cached = dao.observeCatalogue().first()
            val filtered = cached.filter { it.name.contains(query, ignoreCase = true) }
            if (filtered.isEmpty()) throw mapError(it) else filtered.map { e -> e.toDomain() }
        }
    }

    override fun observeFavourites(): Flow<List<Breed>> =
        dao.observeFavourites().map { list -> list.map { it.toDomain() } }

    override fun observeBreed(breedId: String): Flow<Breed?> =
        dao.observeById(breedId).map { it?.toDomain() }

    override suspend fun refreshBreedDetail(breedId: String): Result<Unit> = runCatching {
        val dto = api.getBreed(breedId)
        val isFav = dao.isFavourite(breedId) ?: false
        val existingSortIndex = dao.getById(breedId)?.sortIndex
        dao.insertOrReplace(dto.toEntity(sortIndex = existingSortIndex, isFavourite = isFav))
    }.recoverCatching { throwable ->
        // If we already have a cached copy (from the list), offline viewing
        // still works - only surface the error when there's truly nothing to show.
        if (dao.getById(breedId) != null) Unit else throw mapError(throwable)
    }

    override suspend fun toggleFavourite(breedId: String) {
        val current = dao.isFavourite(breedId) ?: false
        dao.setFavourite(breedId, !current)
    }

    private fun mapError(throwable: Throwable): Throwable = when (throwable) {
        is IOException -> NoConnectivityException(cause = throwable)
        else -> throwable
    }

    class NoConnectivityException(
        message: String = "No internet connection available.",
        cause: Throwable? = null
    ) : IOException(message, cause)
}