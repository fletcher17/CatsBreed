package com.example.catsbreed.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BreedDao {

    @Query("SELECT * FROM breeds WHERE sortIndex IS NOT NULL ORDER BY sortIndex ASC")
    fun observeCatalogue(): Flow<List<BreedEntity>>

    @Query("SELECT * FROM breeds WHERE isFavourite = 1 ORDER BY name ASC")
    fun observeFavourites(): Flow<List<BreedEntity>>

    @Query("SELECT * FROM breeds WHERE id = :id")
    fun observeById(id: String): Flow<BreedEntity?>

    @Query("SELECT * FROM breeds WHERE id = :id")
    suspend fun getById(id: String): BreedEntity?

    @Query("SELECT MAX(sortIndex) FROM breeds")
    suspend fun getMaxSortIndex(): Int?

    @androidx.room.Transaction
    suspend fun upsertCataloguePage(breeds: List<BreedEntity>) {
        breeds.forEach { incoming ->
            val existing = getById(incoming.id)
            insertOrReplace(incoming.copy(isFavourite = existing?.isFavourite ?: false))
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(breed: BreedEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplaceAll(breeds: List<BreedEntity>)

    @Query("UPDATE breeds SET isFavourite = :isFavourite WHERE id = :id")
    suspend fun setFavourite(id: String, isFavourite: Boolean)

    @Query("SELECT isFavourite FROM breeds WHERE id = :id")
    suspend fun isFavourite(id: String): Boolean?
}