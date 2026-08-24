package com.example.catsbreed.di

import com.example.catsbreed.BuildConfig
import com.example.catsbreed.BuildConfig.CAT_API_KEY
import com.example.catsbreed.data.remote.CatApiService
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

private val apiKeyInterceptor = Interceptor { chain ->
    val request = chain.request().newBuilder()
        .addHeader("x-api-key", CAT_API_KEY)
        .build()
    chain.proceed(request)
}

val networkModule = module {
    single {
        HttpLoggingInterceptor().apply{
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    single {
        OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
            .addInterceptor(get<HttpLoggingInterceptor>())
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    single {
        val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(get())
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    single { get<Retrofit>().create(CatApiService::class.java) }
}