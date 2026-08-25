package com.example.catsbreed.di

import com.example.catsbreed.domain.usecase.CalculateAverageLifespanUseCase
import com.example.catsbreed.domain.usecase.LoadBreedsPageUseCase
import com.example.catsbreed.domain.usecase.ObserveBreedDetailUseCase
import com.example.catsbreed.domain.usecase.ObserveBreedsUseCase
import com.example.catsbreed.domain.usecase.ObserveFavouritesUseCase
import com.example.catsbreed.domain.usecase.RefreshBreedDetailUseCase
import com.example.catsbreed.domain.usecase.SearchBreedsUseCase
import com.example.catsbreed.domain.usecase.ToggleFavouriteUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { ObserveBreedsUseCase(get()) }
    factory { LoadBreedsPageUseCase(get()) }
    factory { SearchBreedsUseCase(get()) }
    factory { ToggleFavouriteUseCase(get()) }
    factory { ObserveFavouritesUseCase(get()) }
    factory { ObserveBreedDetailUseCase(get()) }
    factory { RefreshBreedDetailUseCase(get()) }
    factory { CalculateAverageLifespanUseCase() }

}