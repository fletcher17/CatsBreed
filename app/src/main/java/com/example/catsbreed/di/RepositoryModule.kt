package com.example.catsbreed.di

import com.example.catsbreed.data.repository.BreedRepositoryImpl
import com.example.catsbreed.domain.repository.BreedRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<BreedRepository> { BreedRepositoryImpl(get(), get()) }
}