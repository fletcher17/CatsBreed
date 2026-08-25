package com.example.catsbreed.di

import com.example.catsbreed.presentation.breedlist.BreedListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { BreedListViewModel(get(), get(), get(), get()) }
}