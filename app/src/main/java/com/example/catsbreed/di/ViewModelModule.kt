package com.example.catsbreed.di

import com.example.catsbreed.presentation.breedlist.BreedListViewModel
import com.example.catsbreed.presentation.detail.BreedDetailViewModel
import com.example.catsbreed.presentation.favourites.FavouritesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { BreedListViewModel(get(), get(), get(), get()) }
    viewModel { (breedId: String) -> BreedDetailViewModel(breedId, get(), get(), get()) }
    viewModel { FavouritesViewModel(get(), get()) }
}