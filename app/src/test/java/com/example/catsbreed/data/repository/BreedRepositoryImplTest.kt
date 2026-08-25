package com.example.catsbreed.data.repository

import com.example.catsbreed.data.local.BreedDao
import com.example.catsbreed.data.local.BreedEntity
import com.example.catsbreed.data.remote.CatApiService
import com.example.catsbreed.data.remote.dto.BreedDto
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.IOException

private class InMemoryBreedDao : BreedDao {
    val table = mutableMapOf<String, BreedEntity>()
    val catalogueFlow = MutableStateFlow<List<BreedEntity>>(emptyList())
    val favouritesFlow = MutableStateFlow<List<BreedEntity>>(emptyList())

    override fun observeCatalogue(): Flow<List<BreedEntity>> = catalogueFlow
    override fun observeFavourites(): Flow<List<BreedEntity>> = favouritesFlow
    override fun observeById(id: String): Flow<BreedEntity?> = MutableStateFlow(table[id])
    override suspend fun getById(id: String): BreedEntity? = table[id]
    override suspend fun getMaxSortIndex(): Int? = table.values.mapNotNull { it.sortIndex }.maxOrNull()

    override suspend fun upsertCataloguePage(breeds: List<BreedEntity>) {
        breeds.forEach { insertOrReplace(it.copy(isFavourite = table[it.id]?.isFavourite ?: false)) }
    }

    override suspend fun insertOrReplace(breed: BreedEntity) {
        table[breed.id] = breed
        catalogueFlow.value = table.values.filter { it.sortIndex != null }.sortedBy { it.sortIndex }
        favouritesFlow.value = table.values.filter { it.isFavourite }
    }

    override suspend fun insertOrReplaceAll(breeds: List<BreedEntity>) {
        breeds.forEach { insertOrReplace(it) }
    }

    override suspend fun setFavourite(id: String, isFavourite: Boolean) {
        table[id]?.let { insertOrReplace(it.copy(isFavourite = isFavourite)) }
    }

    override suspend fun isFavourite(id: String): Boolean? = table[id]?.isFavourite
}

class BreedRepositoryImplTest {

    private val api = mockk<CatApiService>()
    private val dao = InMemoryBreedDao()
    private val repository = BreedRepositoryImpl(api, dao)

    @Test
    fun `loadBreedsPage caches results and preserves existing favourite flag on refresh`() = runTest {
        val dto = BreedDto(id = "abys", name = "Abyssinian", lifeSpan = "9 - 15")
        coEvery { api.getBreeds(any(), any()) } returns listOf(dto)

        repository.loadBreedsPage(0, 20)
        repository.toggleFavourite("abys")
        repository.loadBreedsPage(1, 20)

        val cached = dao.getById("abys")
        assertThat(cached?.isFavourite).isTrue()
    }

    @Test
    fun `searchBreeds falls back to cached results when the network fails`() = runTest {
        val dto = BreedDto(id = "beng", name = "Bengal", lifeSpan = "12 - 16")
        coEvery { api.getBreeds(any(), any()) } returns listOf(dto)
        repository.loadBreedsPage(0, 20)

        coEvery { api.searchBreeds(any()) } throws IOException("no network")

        val result = repository.searchBreeds("beng")

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()?.single()?.id).isEqualTo("beng")
    }

    @Test
    fun `searchBreeds propagates failure when offline and nothing cached matches`() = runTest {
        coEvery { api.searchBreeds(any()) } throws IOException("no network")

        val result = repository.searchBreeds("nonexistent")

        assertThat(result.isFailure).isTrue()
    }
}
