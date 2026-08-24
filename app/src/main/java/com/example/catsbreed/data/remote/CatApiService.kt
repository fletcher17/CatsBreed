package com.example.catsbreed.data.remote

import com.example.catsbreed.data.remote.dto.BreedDto
import com.example.catsbreed.data.remote.dto.ImageDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CatApiService {

    @GET("breeds")
    suspend fun getBreeds(
        @Query("limit") limit: Int,
        @Query("page") page: Int
    ): List<BreedDto>

    @GET("breeds/search")
    suspend fun searchBreeds(
        @Query("q") query: String
    ): List<BreedDto>

    @GET("breeds/{breed_id}")
    suspend fun getBreed(@Path("breed_id") breedId: String): BreedDto

    @GET("images/search")
    suspend fun getImagesForBreed(
        @Query("breed_ids") breedId: String,
        @Query("limit") limit: Int = 1
    ): List<ImageDto>
}