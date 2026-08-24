package com.example.catsbreed.di

import androidx.room.Room
import com.example.catsbreed.data.local.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "cats_database"
        ).build()
    }
    single { get<AppDatabase>().breedDao() }
}