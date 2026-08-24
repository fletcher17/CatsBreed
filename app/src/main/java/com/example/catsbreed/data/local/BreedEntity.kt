package com.example.catsbreed.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "breeds")
data class BreedEntity(
    @PrimaryKey val id: String,
    val name: String,
    val origin: String,
    val temperament: String,
    val description: String,
    val lifeSpan: String,
    val imageUrl: String?,
    val isFavourite: Boolean = false,
    /** Preserves API fetch order for the list screen; null for entries only ever seen via search/detail. */
    val sortIndex: Int? = null
)
