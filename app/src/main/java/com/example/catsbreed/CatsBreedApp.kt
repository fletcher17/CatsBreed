package com.example.catsbreed

import android.app.Application
import com.example.catsbreed.di.databaseModule
import com.example.catsbreed.di.networkModule
import com.example.catsbreed.di.repositoryModule
import com.example.catsbreed.di.useCaseModule
import com.example.catsbreed.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class CatsBreedApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@CatsBreedApp)
            modules(networkModule, databaseModule, repositoryModule, useCaseModule, viewModelModule)
        }
    }
}