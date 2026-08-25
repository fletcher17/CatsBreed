package com.example.catsbreed.presentation.navigation

object ScreenRoutes {
    const val BREED_LIST = "breed_list"
    const val FAVOURITES = "favourites"
    const val BREED_DETAIL = "breed_detail/{breedId}"
    fun breedDetail(id: String) = "breed_detail/$id"
}